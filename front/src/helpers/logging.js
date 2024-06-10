const IS_DEBUG_MODE = process.env.DEBUG_MODE || 'false';

export const console_log_debug = IS_DEBUG_MODE === 'true' ? console.log : () => {};
