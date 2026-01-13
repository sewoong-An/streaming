import styles from "styles/page/UploadVideo.module.css";
import { useCallback, useEffect, useRef, useState } from "react";
import { authApi } from "api/api";
import { EventSourcePolyfill } from 'event-source-polyfill';

function UploadVideo({ update }) {
  const dragRef = useRef(null);
  const [progress, setProgress] = useState(0);
  const [status, setStatus] = useState('대기 중');

  const changeFile = async (e) => {
    let selectFiles = [];
    if (e.type === "drop") {
      selectFiles = e.dataTransfer.files;
    } else {
      selectFiles = e.target.files;
    }

    const sseId = "user_" + Math.random().toString(36).substring(2, 9);
    const token = localStorage.getItem("accessToken");

    const eventSource = new EventSourcePolyfill(`/api/video/sseConnect/${sseId}`, {
        headers: {
            'Authorization': `Bearer ${token}`
        },
        heartbeatTimeout: 10 * 60 * 1000, // 10분 (연결 유지 시간)
        withCredentials: true // 필요 시 쿠키 포함
    });

    eventSource.addEventListener("progress", (e) => {
      const data = JSON.parse(e.data);
      setProgress(data.percent);
      setStatus("인코딩 중...");

      if (data.status === 'COMPLETE') {
          setStatus("완료됨!");
          eventSource.close(); // 작업 완료 시 연결 종료
      }
    });

    eventSource.onerror = (err) => {
        console.error("SSE 연결 오류:", err);
        eventSource.close();
    };

    eventSource.onopen = () => {
        const form = new FormData();
        form.append("file", selectFiles[0]);
        form.append("sseId", sseId);
        try {
            authApi
                .post("/api/video/upload", form, {
                  headers: { "Content-Type": "multipart/form-data" },
                })
                .then((response) => {
                  update(response.data.videoId);
                })
                .catch(() => {
                });
        } catch (err) {
            setStatus("업로드 실패");
            eventSource.close();
        }
    };
  };

  const handleDragIn = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
  }, []);

  const handleDragOut = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
  }, []);

  const handleDragOver = useCallback((e) => {
    e.preventDefault();
    e.stopPropagation();
  }, []);

  const handleDrop = useCallback(
    (e) => {
      e.preventDefault();
      console.log(e);
      changeFile(e);
    },
    [changeFile]
  );

  const initDragEvents = useCallback(() => {
    if (dragRef.current !== null) {
      dragRef.current.addEventListener("dragenter", handleDragIn);
      dragRef.current.addEventListener("dragleave", handleDragOut);
      dragRef.current.addEventListener("dragover", handleDragOver);
      dragRef.current.addEventListener("drop", handleDrop);
    }
  }, [handleDragIn, handleDragOut, handleDragOver, handleDrop]);

  const resetDragEvents = useCallback(() => {
    if (dragRef.current !== null) {
      dragRef.current.removeEventListener("dragenter", handleDragIn);
      dragRef.current.removeEventListener("dragleave", handleDragOut);
      dragRef.current.removeEventListener("dragover", handleDragOver);
      dragRef.current.removeEventListener("drop", handleDrop);
    }
  }, [handleDragIn, handleDragOut, handleDragOver, handleDrop]);

  useEffect(() => {
    initDragEvents();

    return () => resetDragEvents();
  }, [initDragEvents, resetDragEvents]);

  return (
    <div>
      <div className={styles.progressBox}>
        <div
          className={styles.progress}
          style={{ width: `${progress}%` }}
        ></div>
      </div>
      <input
        type="file"
        id="fileUpload"
        style={{ display: "none" }}
        multiple={false}
        onChange={changeFile}
      />
      <label
        className={styles.uploadBox}
        ref={dragRef}
        htmlFor="fileUpload"
        style={{ cursor: "pointer" }}
      >
        <div className={styles.uploadText}>
          파일을 드래그 앤 드롭 또는 클릭하여 업로드
        </div>
        <div>{status}</div>
      </label>
    </div>
  );
}

export default UploadVideo;
