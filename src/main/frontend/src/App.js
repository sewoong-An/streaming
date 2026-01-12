import React, { useEffect, useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Layout from "./components/layout/Layout";
import Login from "./pages/Login";
import Home from "./pages/Home";
import Join from "./pages/Join";
import TestPage from "./pages/TestPage";
import MyVideos from "./pages/MyVideos";
import WatchVideo from "./pages/WatchVideo";
import SearchVideo from "./pages/SearchVideo";
import History from "./pages/History";
import Channel from "./pages/Channel";
import ChannelVideos from "./pages/ChannelVideos";
import ChannelPlaylist from "./pages/ChannelPlaylist";
import Setting from "./pages/Setting";
import MyPlaylist from "./pages/MyPlaylist";
import PlaylistVideos from "./pages/PlaylistVideos";
import Subscribe from "./pages/Subscribe";

function App() {
  useEffect(() => {}, []);

  return (
    <div>
      <Router>
        <Routes>
          <Route element={<Layout />}>
            <Route path={"/"} element={<Home />} />
            <Route path={"/test"} element={<TestPage />} />
            <Route path={"/subscribe"} element={<Subscribe />} />
            <Route path={"/myVideos"} element={<MyVideos />} />
            <Route path={"/myPlaylist"} element={<MyPlaylist />} />
            <Route path={"/watch/:id"} element={<WatchVideo />} />
            <Route path={"/search"} element={<SearchVideo />} />
            <Route path={"/history"} element={<History />} />
            <Route path={"/playlist/:id"} element={<PlaylistVideos />} />
            <Route path={"/channel/:handle"} element={<Channel />}>
              <Route path={""} element={<ChannelVideos />} />
              <Route path={"playlist"} element={<ChannelPlaylist />} />
            </Route>
            <Route path={"/setting"} element={<Setting />} />
          </Route>
          <Route path={"/login"} element={<Login />} />
          <Route path={"/join"} element={<Join />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;
