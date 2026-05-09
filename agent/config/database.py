"""数据库连接管理"""

from mysql.connector import pooling

from config.settings import settings

_connection_pool: pooling.MySQLConnectionPool | None = None


def _get_pool() -> pooling.MySQLConnectionPool:
    global _connection_pool
    if _connection_pool is None:
        db_config = {
            "host": settings.database.host,
            "user": settings.database.user,
            "password": settings.database.password,
            "database": settings.database.database,
        }
        _connection_pool = pooling.MySQLConnectionPool(
            pool_name="shopchat_pool",
            pool_size=settings.database.pool_size,
            pool_reset_session=True,
            **db_config,
        )
    return _connection_pool


def get_conn():
    return _get_pool().get_connection()
