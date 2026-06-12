# 🧩 TW's Core

TW's Core is a shared API library we're building specifically for the Tenacious Wonder series.  
Think of it as a common toolbox and communication bridge that lets all TW mods work together more smoothly.  
With the exception of the always-standalone **"TW's Decorative Foods,"** every other TW mod will need this little companion to run.

---

## ❓ Why Does It Exist?

To be honest, we've been players too, and we know that slight frustration when a mod page tells you "requires X to be installed first."  
So we want to openly share why we designed it this way.

### ⚡ We Write Faster, You Play Sooner
While developing multiple mods, we noticed a lot of boilerplate code getting written over and over — item registration, network packets, config systems, you name it. By moving all of that shared code into TW's Core, we no longer have to reinvent the wheel with every new project.  
The time we save goes straight into designing unique mechanics, polishing details, and fixing bugs.

### 🔗 A Little Bridge Between Mods
TW's Core isn't just a static code library; it acts as a runtime "data hub." Through its unified interfaces, different TW mods can safely recognize and communicate with each other behind the scenes.  
The more TW mods you add, the more of these natural, surprising interactions emerge — instead of having isolated islands that don't know the others exist.

### 🧭 A More Cohesive, Seamless Experience
One of the biggest immersion breakers is when several mods from the same family feel disconnected — mismatched rules, clashing aesthetics, or progression that never intersects.  
TW's Core tackles this from the ground up. With its built-in integration framework, even mods originally designed independently can recognize each other in-game and generate sensible, interesting interactions. That sense of quiet harmony lets you sink deeper into the world, and it also lays the most critical track for us to eventually combine everything into a complete modpack.

---

## 🌱 A Note on "TW's Decorative Foods"

We've intentionally kept "TW's Decorative Foods" completely independent. It needs neither TW's Core nor any other dependency — just download and play.

---

> In short, TW's Core was never meant to be a barrier. It's simply a foundation we're carefully laying down, so the world of Tenacious Wonder feels more complete, and no single mod has to shine alone.