# PROJECT_RULES.md: Proofolio student app

These rules apply to every change in this project. Read them before each task.

## What this app is
- The student app of Proofolio. A student signs up, picks one of their public GitHub repos, answers 5 questions about their own code by voice or text, and sees a report of verified skills and matched internships.
- Recruiters do not use this app. They use a separate web dashboard.
- The VerifiedBuild API does all the work. This app is only its UI.

## Data and AI
1. All data comes from the VerifiedBuild API through ApiClient.call { ... } (package com.example.data.api). ApiConfig.API_BASE is the only server this app talks to.
2. The app never calls Gemini, Firebase AI, GitHub or any other AI or data service, and holds no API keys.
3. Never invent data: no sample students, companies, recruiters, hackathons, scores, default values (like 85) or "fallback" results. If a call fails, show the API's message and a way to retry. If a value is missing, show nothing or "—".
4. The phone stores only what SessionStore holds (student id, name, GitHub username, viva session id, time limit). Questions, answers and reports always come from the API.
5. Never show a score or a rubric during the viva. Scores appear only on the report, when its status is "done".

## Files you must not change
6. These files were tested against the live API. Do not edit, rename, reformat or "improve" them: data/api/ApiModels.kt, data/api/VerifiedBuildApi.kt, data/api/ApiClient.kt, data/SessionStore.kt, data/ResumePicker.kt, audio/AnswerRecorder.kt, viva/VivaController.kt. If a task seems to need a change there, stop and say exactly what is missing.
7. Do not change Gradle files, signing configs, applicationId, namespace or AndroidManifest.xml. Do not add libraries.
8. Never add an endpoint or a field that isn't in ApiModels.kt.

## Errors and loading
9. Show ApiResult.Err.message exactly as given: the API writes it in plain English. Use code only for logic (for example STUDENT_NOT_FOUND or ALREADY_APPLIED).
10. Every button that starts an API call is disabled until the call finishes. Every screen that loads data has a loading state and an error state with Retry, and lists have an empty state.

## Code structure
11. Screens are composables that take state and callbacks. All state and all API calls live in AppViewModel, and in VivaController for the viva.
12. Keep the existing visual style: the colours in ui/theme/Color.kt, MaterialTheme typography, and ui/components (StatusBadge, SkillChip, ScoreBadge, Floating3DBackground, InteractiveRadarChart).
13. minSdk is 24. Don't use java.time or other API 26+ classes without a version check.

## Words
14. Use plain English and short sentences. Say "verify", "show" and "prove". Never use these words in anything a user sees: detect, catch, cheat, flag, suspicious, integrity, fraud, plagiarism, AI-powered, revolutionary, seamless, leverage, empower.
15. "Pasted" and the build history are neutral facts. Never style them as warnings.
16. The app name is VerifiedBuild.

## Fixed vocabularies (send exactly these values)
Skill tags, in this order, with display names:
backend = Backend, frontend = Frontend, data = Data, ai_ml = AI/ML, devops = DevOps, security = Security

Verified levels, with labels and colours (text colour on background colour):
strong = "Strong" (AccentEmerald on AccentEmeraldLight)
partial = "Partial" (AccentAmber on AccentAmberLight)
not_verified = "Not verified" (AccentRose on AccentRoseLight)
not_shown = "Not shown" (TextTertiaryMuted on NeutralWarmSurfaceVariant)

Interests (send the id, show the label):
ai = AI, data = Data, web = Web, mobile = Mobile, saas = SaaS, fintech = FinTech, edtech = EdTech, hrtech = HR-Tech, security = Security, devtools = Dev tools, growth = Growth, healthcare = Healthcare

Skill chips (show and send exactly this text):
Python, JavaScript, TypeScript, Java, C++, React, Next.js, HTML/CSS, Flutter, Android, Node.js, FastAPI, Django, Flask, Spring, SQL, MongoDB, Pandas, Machine Learning, Deep Learning, LLMs/GenAI, Docker, Cloud (AWS/GCP/Azure), CI/CD, Security

Difficulty labels: easy = Easy, medium = Medium, hard = Hard

## Contract v1.1 additions
- Hackathon results (send exactly): winner = "Winner", finalist = "Finalist", participant = "Participant".
- A hackathon entry is a claim until verified is true. Show unverified entries as "Not verified yet", never as achievements.
- A student can verify a repo they own, or a teammate's or organisation's repo they have commits in. On team repos the questions are about the files they changed.
- The report combines all finished vivas. Use report.vivaInProgress (not SessionStore) to decide whether a viva can be continued.
