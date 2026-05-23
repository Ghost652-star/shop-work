import http from 'k6/http';
import { check, sleep } from 'k6';

// ===== 压测配置 =====
export const options = {
  stages: [
    { duration: '10s', target: 10 },
    { duration: '20s', target: 50 },
    { duration: '20s', target: 100 },
    { duration: '10s', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'],
    http_req_failed: ['rate<0.1'],
  },
};

const BASE_URL = 'http://localhost:8080';

// ===== 测试场景（每个请求用 tags.name 标记，k6 自动按名称分组统计） =====
export default function () {
  const r1 = http.get(`${BASE_URL}/product/list?page=1&size=20`, { tags: { name: '商品列表' } });
  check(r1, { '商品列表 200': (r) => r.status === 200 });
  sleep(0.5);

  const r2 = http.get(`${BASE_URL}/product/1`, { tags: { name: '商品详情' } });
  check(r2, { '商品详情 200': (r) => r.status === 200 });
  sleep(0.3);

  const r3 = http.get(`${BASE_URL}/category/list`, { tags: { name: '分类列表' } });
  check(r3, { '分类列表 200': (r) => r.status === 200 });
  sleep(0.3);

  const r4 = http.get(`${BASE_URL}/product/hot-sales`, { tags: { name: '热销榜单' } });
  check(r4, { '热销榜单 200': (r) => r.status === 200 });
  sleep(0.5);

  const r5 = http.get(`${BASE_URL}/shop/dashboard/sales-trend`, { tags: { name: '销售趋势' } });
  check(r5, { '销售趋势 200': (r) => r.status === 200 });

  const r6 = http.get(`${BASE_URL}/shop/dashboard/order-status`, { tags: { name: '订单状态' } });
  check(r6, { '订单状态 200': (r) => r.status === 200 });
  sleep(1);
}
