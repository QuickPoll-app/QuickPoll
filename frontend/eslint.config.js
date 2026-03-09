const eslint = require("@eslint/js");
const tseslint = require("typescript-eslint");
const angular = require("angular-eslint");

module.exports = tseslint.config(
  {
    files: ["**/*.ts"],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      "@angular-eslint/directive-selector": [
        "error",
        {
          type: "attribute",
          prefix: "app",
          style: "camelCase",
        },
      ],
      "@angular-eslint/component-selector": [
        "error",
        {
          type: "element",
          prefix: "app",
          style: "kebab-case",
        },
      ],

      "no-console": ["warn", { allow: ["error"] }],
      "@angular-eslint/prefer-on-push-component-change-detection": "error",
      "@typescript-eslint/no-useless-constructor": "error",
      "@typescript-eslint/no-empty-function": "error",
      "@typescript-eslint/no-explicit-any": "error",
      "@typescript-eslint/no-unused-vars": "error",
      "@typescript-eslint/explicit-function-return-type": "error",
      "@typescript-eslint/explicit-member-accessibility": [
        "error",
        {
          accessibility: "explicit",
          overrides: {
            constructors: "no-public",
            methods: "explicit",
            properties: "explicit",
            parameterProperties: "explicit",
          },
        },
      ],
      "brace-style": ["error", "1tbs"],
      "padding-line-between-statements": [
        "error",
        {
          blankLine: "always",
          prev: ["const", "let", "var"],
          next: "*",
        },
        {
          blankLine: "any",
          prev: ["const", "let", "var"],
          next: ["const", "let", "var"],
        },
      ],
      "lines-between-class-members": [
        "error",
        "always",
        {
          exceptAfterSingleLine: true,
        },
      ],
    },
  },
  {
    files: ["**/*.html"],
    extends: [...angular.configs.templateRecommended, ...angular.configs.templateAccessibility],
    rules: {
      "@angular-eslint/template/banana-in-box": "error",
      "@angular-eslint/template/no-any": "error",
      "@angular-eslint/template/eqeqeq": "error",
      "@angular-eslint/template/use-track-by-function": "error",
      "@angular-eslint/template/button-has-type": "error",
    },
  }
);