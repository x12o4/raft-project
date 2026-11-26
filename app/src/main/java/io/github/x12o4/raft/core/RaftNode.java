package io.github.x12o4.raft.core;

import io.github.x12o4.raft.rpc.RequestVoteRequest;
import io.github.x12o4.raft.rpc.RequestVoteResponse;
import java.util.HashSet;
import java.util.Set;

public class RaftNode {
    private final String nodeId;
    private final Set<String> votesReceived = new HashSet<>();
    private final Set<String> peerIds;
    private int currentTerm;
    private String votedFor; // corresponds to the nodeId of a candidate that received this node's vote in
                             // the current term
    private Role currentRole;

    public RaftNode(String nodeId, Set<String> peerIds) {
        this.nodeId = nodeId;
        this.peerIds = new HashSet<>(peerIds);
        this.currentTerm = 0;
        this.votedFor = null;
        this.currentRole = Role.FOLLOWER;
    }

    public RaftNode(String nodeId) {
        this(nodeId, Set.of());
    }

    public int majoritySize() {
        return (peerIds.size() + 1) / 2 + 1; // +1 to include self in the count
    }

    public boolean becomeCandidate() {
        if (currentRole == Role.FOLLOWER || currentRole == Role.CANDIDATE) {
            currentTerm++;
            votesReceived.clear();
            votesReceived.add(nodeId);
            votedFor = nodeId;
            currentRole = Role.CANDIDATE;
            return true;
        }
        return false;
    }

    public boolean becomeLeader() {
        if (currentRole == Role.CANDIDATE) {
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

    public RequestVoteResponse handleRequestVote(RequestVoteRequest request) {
        if (request.term() > currentTerm) {
            becomeFollower(request.term());
        } else if (request.term() < currentTerm) {
            return new RequestVoteResponse(currentTerm, false);
        }

        boolean canVote = votedFor == null || votedFor.equals(request.candidateId());
        if (canVote && isCandidateLogUpToDate(request)) {
            votedFor = request.candidateId();
            return new RequestVoteResponse(currentTerm, true);
        }
        return new RequestVoteResponse(currentTerm, false);
    }

    public void handleRequestVoteResponse(RequestVoteResponse response, String voterId) {
        if(response.term() > currentTerm) {
            becomeFollower(response.term());
            return;
        }
        if(currentRole != Role.CANDIDATE) {
            return;
        }
        if(response.term() < currentTerm) {
            return; 
        }
        if(!response.voteGranted()) {
            return;
        }
        votesReceived.add(voterId);
        if(votesReceived.size() >= majoritySize()) {
            becomeLeader();
        }
        
    }
    private boolean isCandidateLogUpToDate(RequestVoteRequest request) {
        return true;
    }
}
