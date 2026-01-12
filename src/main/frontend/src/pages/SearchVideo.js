import { useOutletContext, useSearchParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import VideoCardHorizon from "../components/common/VideoCardHorizon";
import styles from "styles/page/SearchVideo.module.css";

function SearchVideo() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [videos, setVideos] = useState([]);
  const { contentsRef, winSize } = useOutletContext();
  const [offset, setOffset] = useState(0);

  useEffect(() => {
    authApi
      .get(
        "/api/video/getVideos?searchQuery=" + searchParams.get("searchQuery")
      )
      .then((response) => {
        setVideos(response.data.data);
      });
  }, []);

  useEffect(() => {
    if (videos.length === 0) return;

    contentsRef.current.addEventListener("scroll", function () {
      const addScrollTop =
        (contentsRef.current.scrollHeight - contentsRef.current.offsetHeight) *
        0.9;

      if (contentsRef.current.scrollTop >= addScrollTop) {
        if (videos.length >= offset + 12) {
          setOffset((curr) => curr + 12);
        }
      }
    });
  }, [videos]);

  return (
    <div>
      {videos.slice(0, Math.min(videos.length, offset + 12)).map((video) => (
        <div className={styles.video}>
          <VideoCardHorizon
            videoInfo={video}
            imageSize={winSize ? "mini" : ""}
          />
        </div>
      ))}
    </div>
  );
}

export default SearchVideo;
