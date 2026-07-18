(ns packcoord.store
  "SSoT for the ISCO-08 8183 packing, bottling and labelling machine
  operators plant scheduling/logistics coordination actor (itonami
  actor pattern, ADR-2607121000 / CLAUDE.md Actors section; README's
  'Robotics premise' — a plant scheduling/logistics coordination robot
  performs crew scheduling, production-run/inventory/progress-record
  logging and packaging-materials/consumables supply-order
  coordination for a packing/bottling/labelling crew under this
  advisor/governor pair, which never dispatches hardware itself, never
  operates packaging line equipment itself, and never finalizes a
  line-operation-execution decision or a plant-safety-clearance
  decision, and never overrides a plant safety officer's judgment —
  those remain the plant safety officer's exclusive judgment).
  Modeled closely on cloud-itonami-isco-8143's papercoord.store.

  Domain:

    packer    — a registered packing, bottling and labelling machine
                operator crew member who runs high-speed packaging/
                bottling/labelling line equipment (case packers, bottle
                fillers/cappers, labelling machines, wrapping and
                palletizing lines)
                (:packer-id, :name)
    facility  — a registered packaging/bottling line facility
                {:facility-id :name :max-supply-cost number}.
                `:max-supply-cost` is an informational registered
                ceiling used only to decide whether a
                `:coordinate-supply-order` proposal escalates to human
                sign-off (the governor never blocks a
                within-threshold order outright; it only decides
                commit vs. escalate).
    record    — a committed operating record (a logged production-run/
                inventory/progress entry, a scheduled crew/shift
                operation, a flagged safety concern, or a coordinated
                packaging-materials/consumables supply order) — written
                ONLY via commit-record!. This actor coordinates plant
                scheduling/logistics ONLY — a `record` is a
                coordination artifact, never a line-operation-
                execution act, never a plant-safety-clearance
                decision, and never a plant safety officer's-judgment
                override.
    ledger    — append-only audit trail, commit or hold.")

(defprotocol Store
  (packer [s packer-id])
  (facility [s facility-id])
  (records-of [s packer-id])
  (ledger [s])
  (register-packer! [s packer])
  (register-facility! [s facility])
  (commit-record! [s record])
  (append-ledger! [s fact]))

(defrecord MemStore [a]
  Store
  (packer [_ packer-id] (get-in @a [:packers packer-id]))
  (facility [_ facility-id] (get-in @a [:facilities facility-id]))
  (records-of [_ packer-id] (filter #(= packer-id (:packer-id %)) (:records @a)))
  (ledger [_] (:ledger @a))
  (register-packer! [s p]
    (swap! a assoc-in [:packers (:packer-id p)] p) s)
  (register-facility! [s f]
    (swap! a assoc-in [:facilities (:facility-id f)] f) s)
  (commit-record! [s record]
    (swap! a update :records (fnil conj []) record) s)
  (append-ledger! [s fact]
    (swap! a update :ledger (fnil conj []) fact) s))

(defn mem-store
  ([] (mem-store {}))
  ([seed] (->MemStore (atom (merge {:packers {} :facilities {} :records [] :ledger []}
                                    seed)))))
