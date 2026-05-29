from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from pydantic import BaseModel
from typing import Optional
import traceback
import sys
import json
import io

from app.serviceClient import run_service_agent

# 修复Windows终端编码问题
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8', errors='replace')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8', errors='replace')

# 创建 FastAPI 应用实例
app = FastAPI(title="ShopChat API", description="简单的请求处理API")

# 定义请求数据模型
class RequestData(BaseModel):
    message: str
    user_id: Optional[str] = None
    order_no: Optional[str] = None


# POST 请求处理端点
@app.post("/process")
async def process_request(request: Request):
    try:
        body = await request.json()
        data = RequestData(**body)

        # 把 FastAPI 收到的输入交给你的 service agent 处理
        processed_msg = run_service_agent(
            message=data.message,
            user_id=data.user_id,
            order_no=data.order_no
        )

        # 返回处理结果，确保UTF-8编码
        result = {
            "status": "success",
            "processed_message": processed_msg,
            "original_data": {
                "message": data.message,
                "user_id": data.user_id,
                "order_no": data.order_no
            }
        }
        return JSONResponse(
            content=result,
            media_type="application/json; charset=utf-8"
        )
    except Exception as e:
        return JSONResponse(
            content={
                "status": "error",
                "processed_message": f"处理出错: {str(e)}",
                "original_data": {}
            },
            media_type="application/json; charset=utf-8"
        )

# GET 请求处理端点（带查询参数）
# @app.get("/query")
# def query_handler(q: str, name: Optional[str] = None):
#     """
#     处理GET请求
#
#     请求示例:
#     GET http://127.0.0.1:8000/query?q=测试内容&name=张三
#     """
#     result = f"查询内容: {q}"
#     if name:
#         result += f", 用户: {name}"
# 
#     return {
#         "status": "success",
#         "result": result
#     }

if __name__ == "__main__":
    import uvicorn
    # 启动服务器，端口8000，支持热重载
    uvicorn.run(app, host="127.0.0.1", port=8000)
