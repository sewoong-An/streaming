import { Container, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import VideoCard from "components/common/VideoCard";
import { useEffect, useState } from "react";
import { authApi } from "../api/api";
import { useOutletContext } from "react-router-dom";

function Home() {
  const [videos, setVideos] = useState([]);
  const { contentsRef } = useOutletContext();
  const [offset, setOffset] = useState(0);

  useEffect(() => {
    authApi.get("/api/video/getVideos").then((response) => {
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
    <div id={"home"}>
      <Container fluid>
        <Row id={"row"} xs={1} sm={1} md={2} lg={3} xxl={4} className="g-4">
          {videos
            .slice(0, Math.min(videos.length, offset + 12))
            .map((video) => (
              <Col key={video.videoId}>
                <VideoCard
                  videoInfo={video}
                  effHover={true}
                  viewChannel={true}
                />
              </Col>
            ))}
        </Row>
      </Container>
    </div>
  );
}
export default Home;
