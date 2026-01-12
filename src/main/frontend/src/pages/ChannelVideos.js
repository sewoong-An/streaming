import { Col, Row } from "react-bootstrap";
import VideoCard from "../components/common/VideoCard";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import styles from "styles/page/ChannelVideos.module.css";
import { useOutletContext } from "react-router-dom";

function ChannelVideos() {
  const [videos, setVideos] = useState([]);
  const { handle, contentsRef } = useOutletContext();
  const [offset, setOffset] = useState(0);

  useEffect(() => {
    authApi.get("/api/video/getChannelVideos/" + handle).then((response) => {
      setVideos(response.data.data);
    });
  }, [handle]);

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
    <div className={styles.contentsBox}>
      <Row xs={1} sm={1} md={2} lg={3} xxl={4} className="g-4">
        {videos.slice(0, Math.min(videos.length, offset + 12)).map((video) => (
          <Col key={video.videoId}>
            <VideoCard videoInfo={video} />
          </Col>
        ))}
      </Row>
    </div>
  );
}

export default ChannelVideos;
