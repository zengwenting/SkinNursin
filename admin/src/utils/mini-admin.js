import axios from "axios";
import { localCache } from "codingtalk-vue-toolkit";
import { TOKEN_KEY } from "@/config";

const BASE_URL =
  process.env.NODE_ENV === "production"
    ? "/mini/"
    : "http://localhost:4001/mini/";

export async function miniAdminPost(path, body = {}, params = {}) {
  const token = localCache.get(TOKEN_KEY);
  const response = await axios.post(`${BASE_URL}${path}`, body, {
    params,
    headers: token
      ? {
          "x-access-token": token,
        }
      : {},
  });
  return response.data;
}
