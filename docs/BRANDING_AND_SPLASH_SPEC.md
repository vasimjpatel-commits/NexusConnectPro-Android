# NexusConnect Pro — Simple App Branding Specification

## Brand identity

- Application name: **NexusConnect Pro**
- Startup name: **Vasim**
- Creator credit on the startup/welcome screen: **Designed by Vasim J Patel**
- Style: clean, simple, decent and professional.
- No decorative monogram, chat-bubble symbol, network/orbit artwork, tagline, gradients or extra descriptive text in the logo.

## Logo

The logo must contain only the application name:

**NexusConnect Pro**

Use a clear, readable typeface with restrained styling. Keep the same simple wordmark in the launcher/app branding where legibility permits. Do not add “Vasim”, the creator credit, icons, slogans or feature descriptions inside the logo.

## Startup screen

Show a plain, uncluttered startup screen:
- **Vasim**
- **NexusConnect Pro**
- **Designed by Vasim J Patel**

Keep the layout centered, readable and brief. The creator credit belongs on the startup/welcome screen, not inside the logo. Do not add a fake loading delay. Android 12+ should use the platform splash screen behavior and transition promptly to authentication or the restored session.

## Other app surfaces

- About screen: show “NexusConnect Pro” and “Designed by Vasim J Patel”.
- Launcher icon: use a simple typographic treatment of the application name if it remains legible at icon size; otherwise use a plain, minimal app-name initial without decorative detail.
- Play Store assets: keep the same restrained visual identity.

## Implementation checklist

- Put the app name and creator credit in Android string resources.
- Use the exact spelling: “Vasim” and “Designed by Vasim J Patel”.
- Use a restrained background and high-contrast typography.
- Keep startup fast and accessible.
- Do not change authentication, chat, calls, media or GPS behavior as part of branding.
- Do not add large decorative images or media just to increase APK size.

## Acceptance criteria

- The logo contains only “NexusConnect Pro”.
- The startup screen displays “Vasim”, “NexusConnect Pro” and “Designed by Vasim J Patel” in a simple layout.
- No extra logo symbols, tagline or feature details are present.
- Branding does not delay login or session restoration.
