package io.github.x12o4.raft.core;

public class RaftNode {
    private final String nodeId;
    private int currentTerm;
    private RaftNode votedFor;
    private Role currentRole;

    public RaftNode(String nodeId) {
        this.nodeId = nodeId;
        this.currentTerm = 0;
        this.votedFor = null;
        this.currentRole = Role.FOLLOWER;
    }

    public boolean becomeCandidate() {
        if (currentRole == Role.FOLLOWER) {
            currentRole = Role.CANDIDATE;
            return true;
        }
        return false;
    }

    public boolean becomeLeader() {
        if(currentRole == Role.CANDIDATE) {
            currentRole = Role.LEADER;
            return true;
        }
        return false;
    }

    public boolean becomeFollower() {
        if(currentRole == Role.LEADER || currentRole == Role.CANDIDATE) {
            currentRole = Role.FOLLOWER;
            return true;
        }
        return false;
    }
}
