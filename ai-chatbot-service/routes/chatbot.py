from fastapi import APIRouter
from pydantic import BaseModel
from typing import List
from Services.chatbot_service import query_ollama

router = APIRouter()

class ChatMessage(BaseModel):
    role: str
    text: str

class ChatRequest(BaseModel):
    messages: List[ChatMessage]

@router.post("/chat")
async def chat(request: ChatRequest):
    formatted_messages = [{"role": m.role, "content": m.text} for m in request.messages]
    response = query_ollama(formatted_messages)
    return {"response": response}
