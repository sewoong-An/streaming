import { api, authApi } from "api/api";
import { useEffect, useState } from "react";

function TestPage() {
  const [result, setResult] = useState(0);

  const testApi = async () => {
    const sse = new EventSource("/sse/connect");

    // sse.onopen = (event) => {
    //   console.log(event);
    // };
    //
    // sse.onmessage = (event) => {
    //   setResult(event.data);
    //   console.log(event);
    //   // if (event.data === "9") {
    //   sse.close();
    //   // }
    // };

    sse.addEventListener("key", (e) => {
      const { data: key } = e;
      console.log(key);
      authApi.get("sse/sseTest?key=" + key).then((response) => {
        sse.close();
      });
    });

    sse.addEventListener("progress", (e) => {
      const { data: progress } = e;
      setResult(progress);
    });

    //
  };

  useEffect(() => {}, []);
  return (
    <div>
      <button onClick={testApi}>실행</button>
      <div>결과 : {result}</div>
    </div>
  );
}

export default TestPage;
