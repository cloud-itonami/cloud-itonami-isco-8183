# cloud-itonami-isco-8183

Open Occupation Blueprint for **ISCO-08 8183**: Packing, Bottling and
Labelling Machine Operators.

This repository designs a forkable OSS business for a packaging-line
plant scheduling and logistics coordination practice: a plant scheduling
and supply-coordination robot manages crew/task records under a
governor-gated actor, so a packing, bottling and labelling machine
operator crew keeps its own operating records instead of renting a
closed workforce-management SaaS.

**Maturity: `:implemented`.** `src/packcoord/` implements the
`PackCoordActor` as a `langgraph.graph/state-graph` (`packcoord.actor`)
wired to a `Packing, Bottling and Labelling Plant Scheduling
Coordination Advisor` (`packcoord.advisor`) and an independent
`PackCoordGovernor` (`packcoord.governor`), following the itonami actor
pattern (ADR-2607121000): `:intake -> :advise -> :govern -> :decide -+->
:commit (:ok? true) +-> :request-approval (:escalate? true,
human-in-the-loop interrupt) +-> :hold (:hard? true)`. HARD invariants
(always hold, never overridable): packer provenance, facility
provenance, no-actuation (`:effect` must be `:propose`), a closed
op-allowlist (`:log-work-record`, `:schedule-crew-operation`,
`:flag-safety-concern`, `:coordinate-supply-order` — nothing else may
ever be proposed), and a permanent, unconditional block on any proposal
that would directly finalize a line-operation-execution decision (e.g.
deciding to proceed with a specific packing/bottling/labelling run) or a
plant-safety-clearance decision (e.g. declaring the plant safety
cleared), or that would override a plant safety officer's judgment.
Always-escalate paths (human sign-off regardless of confidence, mapping
this repo's Trust Controls in
[`docs/business-model.md`](docs/business-model.md)):
`:flag-safety-concern` (always) and `:coordinate-supply-order` above the
registered cost threshold.

## Robotics premise

All cloud-itonami verticals are designed on the premise that a **robot performs
the physical domain work**. Here a plant scheduling/logistics coordination
robot performs crew scheduling, production-run/inventory/progress-record
logging and packaging-materials/consumables supply-order coordination for a
packing, bottling and labelling machine operator crew, under an actor that
proposes actions and an independent **Packing, Bottling and Labelling Plant
Scheduling Coordination Governor** that gates them. The governor never
dispatches hardware itself, never operates packaging line equipment on the
plant floor, and never finalizes a line-operation-execution decision or a
plant-safety-clearance decision, and never overrides a plant safety
officer's judgment; `:high`/`:safety-critical` actions (such as a flagged
conveyor-entanglement/crush-hazard/machinery-condition/equipment-condition
concern, or an above-threshold supply order) require human sign-off.
**This actor coordinates PLANT SCHEDULING/LOGISTICS ONLY — it never operates
packaging line equipment itself, and it never makes a plant-safety-clearance
decision itself.**

Packing, Bottling and Labelling Machine Operators run high-speed
packaging/bottling lines (case packers, bottle fillers/cappers, labelling
machines, wrapping and palletizing lines) — a real heavy-machinery hazard
domain (entanglement and crush hazard from conveyors and packing
mechanisms). This actor never operates that equipment and never clears it
as safe — it only schedules and logs around it, and always routes
machinery-hazard/safety concerns to a human plant safety officer.

## Core Contract

```text
crew roster + facility registration + safety-reporting policy
        |
        v
Packing, Bottling and Labelling Plant Scheduling Coordination Advisor -> PackCoordGovernor -> log/schedule/coordinate, or human sign-off
        |
        v
robot actions (gated) + operating records + audit ledger
```

No automated advice can dispatch a robot action the governor refuses,
finalize a line-operation-execution decision, finalize a
plant-safety-clearance decision, override a plant safety officer's
judgment, suppress an operating record, or disclose sensitive data without
governor approval and audit evidence.

## Capability layer

Resolves via [`kotoba-lang/occupation`](https://github.com/kotoba-lang/occupation)
(ISCO-08 `8183`). Required capabilities:

- :robotics
- :identity
- :audit-ledger

See [`docs/business-model.md`](docs/business-model.md) and
[`docs/operator-guide.md`](docs/operator-guide.md).

## License

AGPL-3.0-or-later.
