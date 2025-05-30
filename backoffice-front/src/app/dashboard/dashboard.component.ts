import {
  Component,
  OnInit,
  AfterViewInit,
  ViewChild,
  ElementRef
} from '@angular/core';
import Chart, { ChartConfiguration, ChartType, ChartOptions } from 'chart.js/auto';
import { DashboardService, DashboardStatistics } from '../services/dashboard.service';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
  statistics?: DashboardStatistics;
  clients: any[] = [];
  clientRecommendations: { [clientId: number]: any[] } = {};

  @ViewChild('salesCanvas') salesCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('productsCanvas') productsCanvas!: ElementRef<HTMLCanvasElement>;

  private salesChart!: Chart;
  private productsChart!: Chart;

  constructor(private dashSvc: DashboardService) {}

  showBubble = false;
  displayedText: string = '';
  private fullText: string = '';
  private typingIndex: number = 0;
  private typingSpeed = 25;

  chatMessages: { role: 'user' | 'bot'; text: string }[] = [];
  userInput = '';
  chatLoading = false;
  private lastRequestTime = 0;

  warningMessage = '';
  warningTimeout: any;

  ngOnInit(): void {
    this.dashSvc.getStatistics().subscribe({
      next: stats => {
        this.statistics = stats;
        this.startTypingEffect(stats.aiSummary || '');
        if (this.salesChart && this.productsChart) {
          this.updateSalesChart(stats);
          this.updateProductsChart(stats);
        }
      },
      error: err => console.error('Stats load error', err)
    });

    this.loadAcheteurs();
  }

  loadAcheteurs(): void {
    this.dashSvc.getAcheteurClients().subscribe({
      next: (res) => {
        this.clients = res;
        for (let client of res) {
          this.loadRecommendationsForClient(client.codeClient);
        }
      },
      error: (err) => console.error("Erreur chargement clients acheteurs", err)
    });
  }

  loadRecommendationsForClient(clientId: number): void {
    this.dashSvc.getRecommendationsForClient(clientId).subscribe({
      next: (res) => {
        this.clientRecommendations[clientId] = res.recommendations;
      },
      error: (err) => {
        console.error(`Erreur reco IA pour client ${clientId}`, err);
      }
    });
  }

  toggleBubble(): void {
    this.showBubble = !this.showBubble;
    if (this.showBubble && this.statistics?.aiSummary) {
      this.startTypingEffect(this.statistics.aiSummary);
    }
  }

  startTypingEffect(text: string): void {
    this.fullText = text;
    this.displayedText = '';
    this.typingIndex = 0;

    const typeChar = () => {
      if (this.typingIndex < this.fullText.length) {
        this.displayedText += this.fullText[this.typingIndex];
        this.typingIndex++;
        setTimeout(typeChar, this.typingSpeed);
      }
    };

    typeChar();
  }

  sendMessage(): void {
    const now = Date.now();
    if (now - this.lastRequestTime < 30000) {
      this.warningMessage = '⏳ Please wait a few seconds before sending another question.';
      clearTimeout(this.warningTimeout);
      this.warningTimeout = setTimeout(() => this.warningMessage = '', 4000);
      return;
    }

    if (!this.userInput.trim()) return;

    const userMsg = this.userInput.trim();
    this.chatMessages.push({ role: 'user', text: userMsg });
    this.userInput = '';
    this.chatLoading = true;
    this.lastRequestTime = now;

    this.dashSvc.sendChatMessage(userMsg).subscribe({
      next: (res) => {
        this.chatMessages.push({ role: 'bot', text: res.response });
        this.chatLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.chatMessages.push({ role: 'bot', text: '⚠️ Error getting response.' });
        this.chatLoading = false;
      }
    });
  }

  ngAfterViewInit(): void {
    this.salesChart = new Chart(this.salesCanvas.nativeElement, this.salesChartConfig());
    this.productsChart = new Chart(
      this.productsCanvas.nativeElement,
      this.productsChartConfig() as unknown as ChartConfiguration
    );
  }

  private baseOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          boxWidth: 12,
          padding: 16
        }
      }
    },
    scales: {
      x: { grid: { display: false } },
      y: {
        beginAtZero: true,
        ticks: {
          callback: val => (val as number).toLocaleString()
        },
        grid: {
          color: 'rgba(0,0,0,0.05)'
        }
      }
    }
  };

  private salesChartConfig(): ChartConfiguration {
    return {
      type: 'bar' as ChartType,
      data: {
        labels: ['Today', 'This Week', 'This Month'],
        datasets: [
          {
            label: 'Sales',
            data: [0, 0, 0],
            backgroundColor: '#20c9a6'
          },
          {
            label: 'Revenue (DT)',
            data: [0, 0, 0],
            backgroundColor: '#3e7eff'
          }
        ]
      },
      options: this.baseOptions
    };
  }

  private productsChartConfig(): ChartConfiguration<'doughnut', number[], string> {
    return {
      type: 'doughnut',
      data: {
        labels: [],
        datasets: [{
          label: 'Top Products',
          data: [],
          backgroundColor: ['#ff6a88', '#f7b733']
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { position: 'right' }
        },
        cutout: '60%'
      } as any
    }
  }

  private updateSalesChart(data: DashboardStatistics) {
    this.salesChart.data.datasets![0].data = [
      data.salesToday,
      data.salesThisWeek,
      data.salesThisMonth
    ];
    this.salesChart.data.datasets![1].data = [
      data.revenueToday,
      data.revenueThisWeek,
      data.revenueThisMonth
    ];
    this.salesChart.update();
  }

  private updateProductsChart(data: DashboardStatistics) {
    const labels = Object.keys(data.topProducts);
    const values = Object.values(data.topProducts);
    this.productsChart.data.labels = labels;
    this.productsChart.data.datasets![0].data = values;
    this.productsChart.update();
  }
}
