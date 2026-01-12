import axios from "axios";

const api = axios.create({
  headers: {
    "Content-Type": `application/json;charset=UTF-8`,
  },
});

const authApi = axios.create({
  headers: {
    "Content-Type": `application/json;charset=UTF-8`,
  },
});

//request 인터셉터
authApi.interceptors.request.use(
  function (config) {
    //요청 전 처리할 작업
    const token = localStorage.getItem("accessToken");

    if (!token) {
      return config;
    }

    if (config.headers && token) {
      config.headers.authorization = `Bearer ${token}`;
      return config;
    }
  },
  function (error) {
    //요청 실패 시 처리할 작업
    return Promise.reject(error);
  }
);

// response 인터셉터
authApi.interceptors.response.use(
  function (response) {
    //정상응답 -> 리턴
    return response;
  },
  async (error) => {
    //에러
    const config = error.config;
    const status = error.response.status;
    //Unauthorized
    if (status === 401) {
      // 서버에서 보내주는 토큰 만료 코드 : 1003
      if (error.response.data.code === 1003) {
        const originalRequest = config;
        // token refresh 요청
        await axios
          .post(
            `/api/member/refreshToken`, // token refresh api
            {}
          )
          .then((res) => {
            //기존 요청에 새로운 토큰으로 변경
            originalRequest.headers.authorization = `Bearer ${res.data.tokenInfo.accessToken}`;
            //로컬 스토리지에 새로운 토큰 저장
            localStorage.setItem("accessToken", res.data.tokenInfo.accessToken);
          })
          .catch((error) => {
            //에러 시 재로그인을 위해 로그인 페이지로 이동
            window.location.href = "/login";
          });
        // 요청 실패했던 요청 새로운 accessToken으로 재요청
        return axios(originalRequest);
      } else if (error.response.data.code === 1001) {
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export { api, authApi };
