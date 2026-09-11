(ns packcoord.advisor
  "Packing, Bottling and Labelling Plant Scheduling Coordination
  Advisor — proposing a plant scheduling/logistics coordination
  operation (log a work record, schedule a crew operation, flag a
  safety concern, coordinate a packaging-materials/consumables supply
  order) from a crew roster, facility registration and
  safety-reporting policy. Swappable mock/llm; the advisor ONLY
  proposes — `packcoord.governor` independently gates every proposal
  and always escalates safety concerns and above-threshold supply
  orders. The advisor never proposes to directly finalize a
  line-operation-execution decision (e.g. deciding to proceed with a
  specific packing/bottling/labelling run), or a plant-safety-
  clearance decision (e.g. declaring the plant safety cleared), and
  never proposes to override a plant safety officer's judgment — those
  stay permanently out of this actor's scope. Modeled closely on
  cloud-itonami-isco-8143's papercoord.advisor.

  A proposal: {:op :log-work-record|:schedule-crew-operation|
               :flag-safety-concern|:coordinate-supply-order
               :effect :propose :packer-id str :facility-id str
               :cost number :hazard-type kw :task str :stake kw
               :confidence n :rationale str}")

(defprotocol Advisor
  (-advise [advisor store request] "request -> proposal map"))

(defn- rationale-for [op packer-id facility-id hazard-type]
  (case op
    :log-work-record
    (str "logged work record for packer " packer-id " at facility " facility-id)

    :schedule-crew-operation
    (str "scheduled crew operation for packing line at facility " facility-id)

    :flag-safety-concern
    (str "flagged " (name (or hazard-type :hazard)) " concern for packer "
         packer-id " at facility " facility-id " — routed for plant safety officer review")

    :coordinate-supply-order
    (str "coordinated supply order for packer " packer-id " at facility " facility-id)

    (str "proposed " (name op) " for packer " packer-id " at facility " facility-id)))

(defn- infer [_store {:keys [op stake packer-id facility-id cost hazard-type task]
                       :as request}]
  {:op op
   :effect :propose
   :packer-id packer-id
   :facility-id facility-id
   :cost cost
   :hazard-type hazard-type
   :task task
   :stake (or stake :low)
   :confidence (case (or stake :low) :high 0.7 :medium 0.85 :low 0.95)
   :rationale (rationale-for op packer-id facility-id hazard-type)})

(defn mock-advisor []
  (reify Advisor
    (-advise [_ store request] (infer store request))))

(def ^:private system-prompt
  "You are a packing, bottling and labelling machine operators plant
   scheduling/logistics coordination advisor. Given a request, propose
   an :op (one of :log-work-record, :schedule-crew-operation,
   :flag-safety-concern, :coordinate-supply-order), the :packer-id,
   :facility-id, and any :cost/:hazard-type/:task fields, an honest
   :confidence and a :stake. Never propose an op outside this closed
   list, and never propose to directly finalize a
   line-operation-execution decision (e.g. deciding to proceed with a
   specific packing/bottling/labelling run), or a
   plant-safety-clearance decision (e.g. declaring the plant safety
   cleared), or to override a plant safety officer's judgment — those
   are always out of this actor's scope; it coordinates plant
   scheduling/logistics only and never operates packaging line
   equipment or clears the plant for safety itself. Safety concerns
   always require human sign-off regardless of confidence.")

(defn- parse-proposal [content]
  (try
    (let [p (read-string content)]
      (if (map? p)
        (assoc p :effect :propose)
        {:op :unknown :effect :propose :confidence 0.0 :stake :high
         :rationale "unparseable LLM response"}))
    (catch #?(:clj Exception :cljs js/Error) _
      {:op :unknown :effect :propose :confidence 0.0 :stake :high
       :rationale "LLM response parse failure"})))

(defn llm-advisor
  [chat-model model-generate-fn gen-opts]
  (reify Advisor
    (-advise [_ _store request]
      (let [msgs [{:role :system :content system-prompt}
                  {:role :user :content (str "operation request: " (pr-str request))}]
            resp (model-generate-fn chat-model msgs gen-opts)]
        (parse-proposal (:content resp))))))
