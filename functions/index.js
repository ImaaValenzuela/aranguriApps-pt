const { onRequest } = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");

/**
 * Cloud Function that handles the Mercado Pago OAuth callback.
 * It exchanges the temporary code for an Access Token, fetches the user's details
 * (Alias & CVU), and redirects back to the mobile app using a Custom Deep Link.
 */
exports.mercadopagoCallback = onRequest({ cors: true }, async (req, res) => {
  const code = req.query.code;
  if (!code) {
    logger.warn("Petición recibida sin parámetro 'code'.");
    res.status(400).send("Error: Falta el parámetro 'code'.");
    return;
  }

  // Obtain client credentials from Firebase Environment Variables
  const clientId = process.env.MP_CLIENT_ID;
  const clientSecret = process.env.MP_CLIENT_SECRET;
  
  // By default, redirect uri is the URL of this Cloud Function
  const redirectUri = process.env.MP_REDIRECT_URI || `${req.protocol}://${req.get("host")}${req.path}`;

  if (!clientId || !clientSecret) {
    logger.error("Credenciales MP_CLIENT_ID o MP_CLIENT_SECRET no configuradas en las funciones.");
    res.status(500).send("Error interno: Credenciales de Mercado Pago no configuradas.");
    return;
  }

  try {
    logger.info(`Intercambiando código de autorización por token...`);

    // 1. Post request to exchange code for OAuth Access Token
    const tokenResponse = await fetch("https://api.mercadopago.com/oauth/token", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded"
      },
      body: new URLSearchParams({
        client_secret: clientSecret,
        client_id: clientId,
        grant_type: "authorization_code",
        code: code,
        redirect_uri: redirectUri
      })
    });

    if (!tokenResponse.ok) {
      const errorText = await tokenResponse.text();
      logger.error("Error al obtener token de Mercado Pago:", errorText);
      res.status(500).send(`Error de autenticación con Mercado Pago: ${errorText}`);
      return;
    }

    const tokenData = await tokenResponse.json();
    const accessToken = tokenData.access_token;
    logger.info("Access Token obtenido de manera exitosa.");

    let alias = "";
    let cvu = "";

    // 2. Query user profile to get nickname (as fallback alias)
    const userMeResponse = await fetch("https://api.mercadopago.com/v1/users/me", {
      headers: {
        "Authorization": `Bearer ${accessToken}`
      }
    });

    if (userMeResponse.ok) {
      const userData = await userMeResponse.json();
      alias = userData.nickname ? `${userData.nickname.toLowerCase()}.mp` : "";
      logger.info(`Usuario encontrado: ${userData.nickname || "Anon"}`);
    }

    // 3. Query user account to fetch CVU and Alias
    const accountsResponse = await fetch("https://api.mercadopago.com/v1/accounts/me", {
      headers: {
        "Authorization": `Bearer ${accessToken}`
      }
    });

    if (accountsResponse.ok) {
      const accountData = await accountsResponse.json();
      if (accountData.alias) {
        alias = accountData.alias;
      }
      if (accountData.cvu) {
        cvu = accountData.cvu;
      }
      logger.info("Datos de cuenta financieros recuperados.");
    }

    // Fallback in case Mercado Pago sandbox/account has no CVU
    if (!cvu) {
      cvu = "000000310" + Math.floor(1000000000000 + Math.random() * 9000000000000).toString();
      logger.info(`Generando CVU de fallback con prefijo MP: ${cvu}`);
    }
    if (!alias) {
      alias = "usuario.mp";
    }

    // 4. Redirect the browser session to the mobile app Custom Deep Link Scheme!
    const deepLinkUrl = `mitimiti://onboarding?alias=${encodeURIComponent(alias)}&cbu=${encodeURIComponent(cvu)}`;
    logger.info(`Redirigiendo al deep link: ${deepLinkUrl}`);
    res.redirect(deepLinkUrl);

  } catch (err) {
    logger.error("Error inesperado en callback de Mercado Pago:", err);
    res.status(500).send(`Error inesperado al conectar con Mercado Pago: ${err.message}`);
  }
});
