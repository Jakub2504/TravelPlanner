# Contributing to Travel Planner

Thank you for considering contributing to **Travel Planner**! This guide will help you set up the project, follow our branching strategy, and collaborate efficiently.

---

## 📌 1. Branching Strategy
We follow the **Git Flow** branching model to maintain a structured development workflow:  

- **`main`** – Stable production-ready code.  
- **`develop`** – Active development branch with the latest updates.  
- **Feature branches (`feature/your-feature`)** – For developing new features.  
- **Bugfix branches (`bugfix/your-fix`)** – For fixing bugs in `develop`.  
- **Hotfix branches (`hotfix/your-hotfix`)** – Critical fixes applied directly to `main`.  

### 🔹 **Example Workflow**
```sh
# Switch to develop
git checkout develop 

# Create a feature branch
git checkout -b feature/add-trip-screen 

# Make changes, then commit
git commit -m "Added trip list screen"

# Push the branch to GitHub
git push origin feature/add-trip-screen

