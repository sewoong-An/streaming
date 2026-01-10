package com.sewoong.streaming.repository;

import com.sewoong.streaming.domain.PlaylistVideo;
import com.sewoong.streaming.domain.PlaylistVideoPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistVideoRepository extends JpaRepository<PlaylistVideo, PlaylistVideoPK> {

    Optional<PlaylistVideo> findFirstByPlaylistVideoPKPlaylistId(Integer playlistId);

    List<PlaylistVideo> findAllByPlaylistVideoPKPlaylistIdAndVideo_state(Integer playlistId, String state);
}
