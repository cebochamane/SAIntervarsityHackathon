🪙 StokvelChain – South African Intervarsity Hackathon 2025

StokvelChain is our hackathon project: a blockchain-powered stokvel savings platform designed for South African communities.
It brings trust, transparency, and gamification into traditional savings groups to reduce fraud and encourage consistent contributions.

📂 Repository Structure
├── assets/                 # Images, sponsor logos, supporting visuals
├── demo/                   # Pitch deck + demo video + overview
├── docs/                   # Team info, setup, usage, acknowledgements
├── scripts/                # Utility scripts
├── src/
│   ├── blockchain/         # Core blockchain classes (Wallet, Tx, NoobChain, etc.)
│   └── stokvel/            # Stokvel logic (Group, Member, Ledger, Service, MiniUI)
├── vendor/                 # Third-party jars (e.g., bcprov.jar for BouncyCastle)
├── lib/                    # Local dependency folder
├── out/                    # Compiled .class files (ignored by git)
├── LICENSE
└── README.md

🚀 Features

Blockchain Ledger – immutable record of contributions and payouts.

Stokvel Groups – create a group with monthly contribution rules.

Member Wallets – cryptographic wallets per member (keys via BouncyCastle).

Ledger – timestamped, transparent contributions and payouts.

Gamification – trust scores, streaks, and badges for fraud-prevention + saving consistency.

MiniUI (Swing) – lightweight demo interface for group creation, contributions, payouts, and logs.

🖥️ Demo Flow

Create group (e.g., “Amathuba”, R200 monthly).

Add members (wallets auto-generated).

Fund treasury (demo airdrop for judges).

Contribute (member deposits recorded).

Payout (to selected member).

View ledger (full history).

Gamification: see leaderboard (trust scores, streaks).

🛠️ Tech Stack

Java 17

Swing for demo UI

BouncyCastle (bcprov.jar) for cryptography

Makefile / PowerShell for builds

📑 Documentation

docs/SETUP.md
 → Setup instructions

docs/USAGE.md
 → How to run + demo flow

docs/TEAM.md
 → Team members + roles(i am the sole contributor)

docs/ACKNOWLEDGEMENTS.md
 → Libraries + references

📹 Demo Materials

PowerPoint pitch deck → demo/Stokvel_Pitch.pptx

Demo video → demo/ (to be added)

💡 Impact

Promotes financial inclusion in rural areas.

Makes stokvels fraud-resistant and transparent.

Rewards members with gamification mechanics for trust and consistency.

Simple UI for judges + potential for future mobile-first rollout.

👥 Team

Cebolenkosi Chamane

I used an already existing blockchain i had created earlier this year as the blockchain part of the project
