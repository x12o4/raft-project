package io.github.x12o4.raft.rpc;

public record RequestVoteRequest(int term, String candidateId, int lastLogIndex, int lastLogTerm) {
}
