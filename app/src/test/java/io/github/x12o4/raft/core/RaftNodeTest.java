package io.github.x12o4.raft.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import io.github.x12o4.raft.rpc.RequestVoteRequest;
import io.github.x12o4.raft.rpc.RequestVoteResponse;

class RaftNodeTest {
    @Test
    void candidateCanStartAnotherElectionAfterTimeout() {
        RaftNode node = new RaftNode("node-1");

        assertTrue(node.becomeCandidate());
        assertTrue(node.becomeCandidate());
        assertEquals(2, node.getCurrentTerm());
        assertTrue(node.becomeLeader());
    }

    @Test
    void leaderCannotStartAnElection() {
        RaftNode node = new RaftNode("node-1");

        assertTrue(node.becomeCandidate());
        assertTrue(node.becomeLeader());

        assertFalse(node.becomeCandidate());
    }

    @Test 
    void returnCorrectID() {
        RaftNode node = new RaftNode("abc");
        assertEquals("abc", node.getNodeId());
    }

    @Test
    void returnCorrectTerm() {
        RaftNode node = new RaftNode("abc");
        assertEquals(0, node.getCurrentTerm());
        node.becomeCandidate();
        assertEquals(1, node.getCurrentTerm());
    }

    @Test
    void returnCorrectVotedFor() {
        RaftNode node = new RaftNode("abc");
        assertNull(node.getVotedFor());
        node.becomeCandidate();
        assertEquals("abc", node.getVotedFor());
    }

    @Test
    void returnCorrectRole() {
        RaftNode node = new RaftNode("abc");
        assertEquals(Role.FOLLOWER, node.getCurrentRole());
        node.becomeCandidate();
        assertEquals(Role.CANDIDATE, node.getCurrentRole());
        node.becomeLeader();
        assertEquals(Role.LEADER, node.getCurrentRole());
    }

    @Test 
    void followerCannotBecomeLeader() {
        RaftNode node = new RaftNode("abc");
        node.becomeFollower(1);
        assertFalse(node.becomeLeader());
    }

    @Test 
    void becomeFollower() {
        RaftNode node = new RaftNode("abc");
        node.becomeCandidate();
        assertTrue(node.becomeFollower(2));
        assertEquals(Role.FOLLOWER, node.getCurrentRole());
        assertEquals(2, node.getCurrentTerm());
        assertNull(node.getVotedFor());
    }

    @Test 
    void sameTerm() {
        RaftNode node = new RaftNode("abc");
        node.becomeCandidate();
        node.becomeFollower(node.getCurrentTerm());
        assertEquals(node.getNodeId(), node.getVotedFor());
        assertEquals("abc", node.getVotedFor());
        assertEquals(Role.FOLLOWER, node.getCurrentRole());
    }

    @Test 
    void lowerTerm() {
        RaftNode node = new RaftNode("abc");
        node.becomeCandidate();
        assertFalse(node.becomeFollower(node.getCurrentTerm() - 1));
        assertEquals(Role.CANDIDATE, node.getCurrentRole());
    }

    @Test 
    void leaderChangesOnHigherTerm() {
        RaftNode node = new RaftNode("abc");
        node.becomeCandidate();
        node.becomeLeader();
        assertTrue(node.becomeFollower(node.getCurrentTerm() + 1));
        assertEquals(Role.FOLLOWER, node.getCurrentRole());
    }

    @Test
    void higherTermRequestMakesNodeStepDownAndGrantVote() {
        RaftNode node = new RaftNode("abc");
        RequestVoteResponse response = node.handleRequestVote(new RequestVoteRequest(10, "def", 0, 0));

        assertEquals(Role.FOLLOWER, node.getCurrentRole());
        assertEquals(10, node.getCurrentTerm());
        assertEquals(10, response.term());
        assertTrue(response.voteGranted());
        assertEquals("def", node.getVotedFor());
    }

    @Test
    void lowerTermRequestIsRefused() {
        RaftNode node = new RaftNode("abc");
        node.becomeCandidate(); // term 1, voted for itself

        RequestVoteResponse response = node.handleRequestVote(new RequestVoteRequest(0, "def", 0, 0));

        assertFalse(response.voteGranted());
        assertEquals(1, response.term());
        assertEquals(Role.CANDIDATE, node.getCurrentRole());
        assertEquals("abc", node.getVotedFor());
    }

    @Test
    void secondCandidateInSameTermIsRefused() {
        RaftNode node = new RaftNode("abc");

        assertTrue(node.handleRequestVote(new RequestVoteRequest(1, "def", 0, 0)).voteGranted());
        assertFalse(node.handleRequestVote(new RequestVoteRequest(1, "ghi", 0, 0)).voteGranted());
        assertEquals("def", node.getVotedFor());
    }

    @Test
    void sameCandidateAskingTwiceIsGrantedBothTimes() {
        RaftNode node = new RaftNode("abc");
        RequestVoteRequest request = new RequestVoteRequest(1, "def", 0, 0);

        assertTrue(node.handleRequestVote(request).voteGranted());
        assertTrue(node.handleRequestVote(request).voteGranted());
    }
}
