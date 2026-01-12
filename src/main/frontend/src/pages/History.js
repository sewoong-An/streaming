import styles from "../styles/page/History.module.css";
import { authApi } from "../api/api";
import { useEffect, useState } from "react";
import VideoCardHorizon from "../components/common/VideoCardHorizon";
import * as common from "common.js";
import { useOutletContext } from "react-router-dom";

function History() {
  const [histories, setHistories] = useState([]);
  const { contentsRef, winSize } = useOutletContext();
  const [offset, setOffset] = useState(0);

  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  let currDate = null;

  const WEEKDAY = [
    "일요일",
    "월요일",
    "화요일",
    "수요일",
    "목요일",
    "금요일",
    "토요일",
  ];

  const getHistories = async () => {
    await authApi.get("/api/history/getHistories").then((response) => {
      setHistories(response.data.data);
    });
  };

  const setWatchDt = (date) => {
    const watchDate = new Date(date.split(" ")[0]);
    let result = "";
    if (common.isSameDate(today, watchDate)) {
      result = "오늘";
    } else if (
      (today.getTime() - watchDate.getTime()) / (1000 * 60 * 60 * 24) <=
      1
    ) {
      result = "어제";
    } else if (
      (today.getTime() - watchDate.getTime()) / (1000 * 60 * 60 * 24) <=
      today.getDay()
    ) {
      result = WEEKDAY[watchDate.getDay()];
    } else {
      result = date.split(" ")[0];
    }
    currDate = date.split(" ")[0];
    return result;
  };

  const deleteHistory = (e, videoId) => {
    e.stopPropagation();
    authApi.delete("/api/history/deleteHistory/" + videoId).then((response) => {
      getHistories().then();
    });
  };

  useEffect(() => {
    getHistories().then();
  }, []);

  useEffect(() => {
    if (histories.length === 0) return;

    contentsRef.current.addEventListener("scroll", function () {
      const addScrollTop =
        (contentsRef.current.scrollHeight - contentsRef.current.offsetHeight) *
        0.9;

      if (contentsRef.current.scrollTop >= addScrollTop) {
        if (histories.length >= offset + 12) {
          setOffset((curr) => curr + 12);
        }
      }
    });
  }, [histories]);

  return (
    <div>
      <div className={styles.pageTitle}>시청 기록</div>
      <div>
        {histories
          .slice(0, Math.min(histories.length, offset + 12))
          .map((history) => (
            <div className={styles.video}>
              {currDate !== history.watchDt.split(" ")[0] ? (
                <div className={styles.date}>{setWatchDt(history.watchDt)}</div>
              ) : null}
              <VideoCardHorizon
                videoInfo={history}
                deleteFun={deleteHistory}
                imageSize={winSize ? "mini" : ""}
              />
            </div>
          ))}
      </div>
    </div>
  );
}

export default History;
