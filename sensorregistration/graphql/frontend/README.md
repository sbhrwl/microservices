# SensorRegistrationFrontend
- This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 21.1.0.
## Installation and Running Instructions
```bash
# Navigate to frontend directory
cd C:\Git\microservices\sensorregistration\graphql\frontend

# Install dependencies
npm install

# Start development server
npm start

# Access at http://localhost:4200
```

## Testing Checklist
- Before marking as "done", verify:
  - ✅ Register Sensor - All fields required, clears on success, shows friendly errors
  - ✅ List Sensors - Shows loading state, handles empty results gracefully, displays table
  - ✅ Update Postcode - Validates input, shows success message, handles "same postcode" error
  - ✅ Error Handling - All errors are friendly and inline
  - ✅ No GraphQL in Components - All queries/mutations in separate files
  - ✅ No Auto-refresh - Lists don't update automatically (intentional)
  - ✅ Compiles Successfully - No TypeScript errors
- This completes the Angular frontend build following your exact specifications. The application is ready for manual testing against the GraphQL gateway running on http://localhost:4000/graphql.

## Development server
To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Vitest](https://vitest.dev/) test runner, use the following command:

```bash
ng test
```

## Running end-to-end tests

For end-to-end (e2e) testing, run:

```bash
ng e2e
```

Angular CLI does not come with an end-to-end testing framework by default. You can choose one that suits your needs.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
