from fastapi import APIRouter
from Services.competitive_service import compare_product

router = APIRouter()

@router.get("/compare/{product_id}")
def compare_prices(product_id: int):
    return compare_product(product_id)
