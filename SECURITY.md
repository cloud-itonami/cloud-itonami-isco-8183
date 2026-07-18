# Security Policy

This project handles packing, bottling and labelling machine operator
operating workflows. Treat vulnerabilities as potentially high impact even
when the demo data is synthetic — this domain's failure modes include real
heavy-machinery hazard from high-speed packaging/bottling lines (case
packers, bottle fillers/cappers, labelling machines, wrapping and
palletizing lines), including conveyor-entanglement and crush-hazard risk,
alongside general physical worker-safety risk.

## Do Not Disclose Publicly

Report privately before opening public issues for:

- credential exposure
- real packer, facility or operator data exposure
- authorization bypass
- Packing, Bottling and Labelling Plant Scheduling Coordination Governor bypass
- audit-ledger tampering
- over-disclosure in reports or exports
- unsafe robot action dispatch
- any path that lets a proposal reach a line-operation-execution
  decision, a plant-safety-clearance decision, or a
  plant-safety-officer-override decision

## Reporting

Use GitHub private vulnerability reporting when available for the repository.
If that is unavailable, contact the repository maintainers through the
cloud-itonami organization before publishing details.

Include:

- affected commit or version
- reproduction steps
- expected and actual behavior
- impact on packer/facility data, policy enforcement or audit logging
- suggested fix, if known

## Production Guidance

- Store secrets outside Git.
- Keep real packer/facility/operator data outside this repository.
- Run policy tests before deployment.
- Export and review audit logs regularly.
- Use least privilege for operators and service accounts.
