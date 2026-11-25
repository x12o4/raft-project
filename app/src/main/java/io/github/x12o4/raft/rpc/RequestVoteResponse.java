package io.github.x12o4.raft.rpc;

public record RequestVoteResponse(int term, boolean voteGranted) {
}
