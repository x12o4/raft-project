package io.github.x12o4.raft.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaftNodeTest {
    @Test
    void candidateCanStartAnotherElectionAfterTimeout() {
        RaftNode node = new RaftNode("node-1");

        assertTrue(node.becomeCandidate());
        assertTrue(node.becomeCandidate());
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
        assertEquals(null, node.getVotedFor());
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
    void leaderCannotBecomeCandidate() {
        RaftNode node = new RaftNode("abc");
        node.becomeLeader();
        assertFalse(node.becomeCandidate());
    }
}
