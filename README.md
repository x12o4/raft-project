# Raft (Java)

A from-scratch implementation of the [Raft consensus algorithm](https://raft.github.io/raft.pdf) in Java, built to learn how distributed consensus works.

> **Status:** early development. Nothing below is claimed as done until its box is ticked.

## Goals

- Implement Raft as described in the extended paper (Figure 2 is the spec).
- Keep the core logic independent of the network, so it can be tested deterministically with simulated message loss, delays and partitions.
- Build a small replicated key-value store on top.

## Roadmap

- [ ] Node state and role transitions (follower, candidate, leader)
- [ ] Leader election (`RequestVote`, randomised election timeouts, heartbeats)
- [ ] Log replication (`AppendEntries`, commit index, applying entries)
- [ ] Persistence (`currentTerm`, `votedFor`, `log`) and crash recovery
- [ ] Replicated key-value store with a client
- [ ] Real network transport (replacing the in-memory one)
- [ ] Stretch: snapshots / log compaction, linearizable reads, membership changes

## Design notes

- **Transport interface:** nodes talk through an abstraction. The first implementation is in-memory, used for tests.
- **Concurrency:** each node serialises its state changes (single lock or event loop) to avoid races.

## Build and test

Requires Java 21 and Gradle.

```bash
./gradlew build
./gradlew test
```

## References

- Ongaro and Ousterhout, [*In Search of an Understandable Consensus Algorithm*](https://raft.github.io/raft.pdf)
- [Raft visualisation](https://raft.github.io/)
- Ongaro, *Consensus: Bridging Theory and Practice* (PhD thesis)

## Licence

MIT
