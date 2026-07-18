# Contributing

`cloud-itonami-isco-8183` accepts contributions to the OSS actor, policy tests,
documentation, examples and open occupation blueprint.

## Development

```bash
clojure -M:test
```

Keep changes small and include tests for policy, audit, store or disclosure
behavior.

## Rules

- Do not commit real packer, facility or operator data, credentials or operating
  documents.
- Keep production writes and disclosures behind Packing, Bottling and Labelling Plant Scheduling Coordination Governor.
- Treat this occupation's workflows as high-risk: add tests for permission,
  scope-exclusion, safety-escalation and audit logging.
- Never widen the closed op-allowlist to include a
  line-operation-execution op, a plant-safety-clearance op, or a
  plant-safety-officer-override op, without a dedicated ADR and explicit
  human review.
- Document any new business-model or operator assumption in `docs/`.

## Pull Requests

PRs should describe:

- what behavior changed
- which policy invariant is affected
- how it was tested
- whether operator or certification docs need updates
