# Making Jobs tolerant to faults

Batch jobs are never executed in isolation. they consume/produce data and often interact with external components and services. This interaction exposes the job to different kinds of errors (human or system).

