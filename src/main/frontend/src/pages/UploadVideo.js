import styles from "styles/page/UploadVideo.module.css";
import { useCallback, useEffect, useRef, useState } from "react";
import { authApi } from "api/api";

function UploadVideo({ update }) {
  const dragRef = useRef(null);
  const [progress, setProgress] = useState(0);

  const changeFile = (e) => {
    let selectFiles = [];
    if (e.type === "drop") {
      selectFiles = e.dataTransfer.files;
    } else {
      selectFiles = e.target.files;
    }

    const form = new FormData();
    form.append("file", selectFiles[0]);

    const sse = new EventSource("/sse/connect");

    sse.addEventListener("key", (e) => {
      const { data: key } = e;
      form.append("key", key);
      authApi
        .post("/api/video/upload", form, {
          headers: { "Content-Type": "multipart/form-data" },
        })
        .then((response) => {
          update(response.data.videoId);
          sse.close();
        })
        .catch(() => {
          sse.close();
        });
    });

    sse.addEventListener("progress", (e) => {
      const { data: progress } = e;
      setProgress(Number(progress));
    });
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
      </label>
    </div>
  );
}

export default UploadVideo;
