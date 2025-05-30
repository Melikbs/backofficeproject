from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from routes import chatbot, competitive

app = FastAPI(title="Ooredoo AI Agents")

# CORS (adapt for your frontend)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Routers
app.include_router(chatbot.router, prefix="/api")
app.include_router(competitive.router, prefix="/api")


