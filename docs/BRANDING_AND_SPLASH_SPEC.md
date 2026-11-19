# NexusConnect Pro — App Branding and Splash Screen Specification

## Brand identity
- Product name: **NexusConnect Pro**
- Startup name emphasis: **Vasim**
- Creator credit: **Designed by Vasim J Patel**
- Visual direction: premium, modern, connected/global; deep black/emerald background with metallic gold accents and crisp white typography.
- Primary mark: a distinctive gold letter V integrated with a green chat bubble and a subtle network/orbit motif.
- Brand line: **Chat • Call • Share • Connect**

## Required app surfaces
1. **Launcher icon:** simplified V + chat/network symbol only. No small text or feature labels; it must remain legible at small sizes. Export adaptive foreground/background layers and monochrome icon assets.
2. **Native splash screen:** show the NexusConnect Pro mark, the word **Vasim**, and the exact creator credit **Designed by Vasim J Patel**. Keep it short and non-blocking; do not fake a long loading/progress bar. Android 12+ system splash should use the platform SplashScreen API/theme and transition quickly to the app.
3. **First-launch welcome screen:** optional fuller brand panel with “Vasim” and “Designed by Vasim J Patel”, then continue to sign-in or a restored session. Do not show it on every launch unless the user chooses that behavior.
4. **About screen:** display “NexusConnect Pro” and “Designed by Vasim J Patel” in app settings.
5. **Store assets:** create separate adaptive icon, Play Store icon, feature graphic and screenshots. A splash poster is not a launcher icon.

## Native Android implementation checklist
- Add AndroidX/Android 12 SplashScreen-compatible theme and a Theme.SplashScreen starting theme.
- Configure splash background and a vector/adaptive icon resource; keep startup fast.
- Generate launcher assets at Android mipmap density sizes and adaptive icon foreground/background/monochrome resources.
- Add a Compose WelcomeScreen with accessible text and one continuation action.
- Keep the brand name and creator credit in string resources, not hard-coded in multiple screens.
- Use exact spelling/capitalization: “Vasim” and “Designed by Vasim J Patel”.
- Ensure the splash does not delay authentication, session restoration, or deep links.
- Respect reduced motion and contrast/accessibility settings.
- Do not use a full poster as the tiny launcher icon; derive a simplified vector mark for clarity.

## Acceptance criteria
- App launcher displays the simplified NexusConnect Pro icon.
- On cold start, Android splash displays “Vasim” and “Designed by Vasim J Patel” with the app mark.
- Native welcome screen, if enabled, displays the same branding consistently.
- Existing login, OTP, chat, calling and GPS navigation are not blocked or reset by branding.
- No artificial loading delay is introduced to display branding.
- Final assets are original or appropriately licensed and checked for legibility on light/dark launcher backgrounds.

## Asset note
Concept artwork generated during planning is a visual reference, not yet an Android-ready launcher asset or committed binary resource. Before implementation, prepare a clean vector/app-icon version and properly sized splash artwork, then add the generated Android resource files to the project.