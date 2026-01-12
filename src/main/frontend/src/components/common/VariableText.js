import { useEffect, useRef, useState } from "react";

function VariableText({ text, limit }) {
  const [more, setMore] = useState(false);
  const [isOverflow, setIsOverflow] = useState(null);
  const Ref = useRef();

  useEffect(() => {
    if (isOverflow == null) {
      setIsOverflow(Ref.current.offsetHeight < Ref.current.scrollHeight);
    }
  });

  const changeMore = () => {
    setMore((curr) => !curr);
  };

  const viewAll = {
    paddingTop: "10px",
    whiteSpace: "pre-wrap",
    wordBreak: "break-all",
    wordWrap: "break-word",
  };

  const view = {
    paddingTop: "10px",
    overflow: "hidden",
    webkitLineClamp: limit,
    display: "-webkit-box",
    webkitBoxOrient: "vertical",
    textOverflow: "ellipsis",
    border: "none",
    whiteSpace: "pre-wrap",
    wordBreak: "break-all",
    wordWrap: "break-word",
  };

  return (
    <div>
      <div style={more ? viewAll : view} ref={Ref}>
        {text}
      </div>
      <div>
        {isOverflow ? (
          <a
            style={{ color: "#686868", fontSize: "0.9rem" }}
            onClick={changeMore}
          >
            {more ? "간략히" : "더보기"}
          </a>
        ) : null}
      </div>
    </div>
  );
}

export default VariableText;
