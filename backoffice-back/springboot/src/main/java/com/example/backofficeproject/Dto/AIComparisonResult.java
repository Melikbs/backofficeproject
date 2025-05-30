package com.example.backofficeproject.Dto;

import java.util.List;

public class AIComparisonResult {
    public int product_id;
    public String product_name;
    public List<CompetitorResult> results;
    public String suggestion;

    public static class CompetitorResult {
        public String site;
        public String price;
        public String url;
    }
}