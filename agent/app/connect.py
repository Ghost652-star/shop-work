from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional

from app.serviceClient import run_service_agent


# 创建 FastAPI 应用实例
app = FastAPI(title="ShopChat API", description="简单的请求处理API")

# 定义请求数据模型
class RequestData(BaseModel):
    message: str
    user_id: Optional[str] = None


# POST 请求处理端点
@app.post("/process")
def process_request(data: RequestData):
    # 把 FastAPI 收到的输入交给你的 service agent 处理
    processed_msg = run_service_agent(message=data.message, user_id=data.user_id)

    # 返回处理结果
    return {
        "status": "success",
        "processed_message": processed_msg,
        "original_data": {
            "message": data.message,
            "user_id": data.user_id
        }
    }

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
