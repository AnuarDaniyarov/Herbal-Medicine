# Knowledge data review status

The seed claims in V9 and V12 are not evidence of expert review. Migration V14 downgrades any claim without a reviewer, review timestamp, and matching review event to `UNVERIFIED`; the database constraint also rejects verified claims without a reviewer and timestamp. The application serves claims to the assistant and public claim endpoints only when their review status is `VERIFIED`.

No human expert/source review was performed as part of the repository changes. Accordingly, the seeded health-related claims must remain unverified and are not represented as clinically established. An authorized subject-matter expert must check each claim against its cited primary source, confirm the exact locator and wording, and record the decision through the expert review flow before it can become visible as verified. Unsupported claims or citations should be rejected or removed during that review.

This status is intentional: assigning a synthetic reviewer or marking seed content `SOURCE_CITED` without evidence would misrepresent the review that actually occurred. This document does not validate any source, DOI, efficacy statement, or dataset redistribution license.
