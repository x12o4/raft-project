package io.github.x12o4.raft.core;

public class RaftNode {
    private final String nodeId;
    private int currentTerm;
    private String votedFor; // corresponds to the nodeId of a candidate that received this node's vote in the current term
    private Role currentRole;

    public RaftNode(String nodeId) {
        this.nodeId = nodeId;
        this.currentTerm = 0;
        this.votedFor = null;
        this.currentRole = Role.FOLLOWER;
    }

    public boolean becomeCandidate() {
        if (currentRole == Role.FOLLOWER || currentRole == Role.CANDIDATE) {
            currentTerm++;
            votedFor = nodeId;
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

    public boolean becomeFollower(int term) {
        if (term > currentTerm) {
            currentTerm = term;
            votedFor = null;
            currentRole = Role.FOLLOWER;
            return true;
        } else if (term == currentTerm) {
            currentRole = Role.FOLLOWER;
            return true;
        }
        return false;
    }

    public String getNodeId() {
        return nodeId;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public String getVotedFor() {
        return votedFor;
    }

    public Role getCurrentRole() {
        return currentRole;
    }

    
}
