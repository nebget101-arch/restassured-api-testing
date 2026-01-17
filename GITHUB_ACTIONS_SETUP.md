# GitHub Actions Setup - Troubleshooting Guide

## ✅ Fix for "Failed to push" Error

The GitHub Actions workflow needs proper permissions to deploy to GitHub Pages. Follow these steps:

---

## 🔧 Step-by-Step Fix

### 1. Update Workflow Permissions

Go to your repository on GitHub:
1. Click **Settings** (top navigation)
2. Click **Actions** in left sidebar
3. Click **General**
4. Scroll to **Workflow permissions** section
5. Select **"Read and write permissions"**
6. Check ✅ **"Allow GitHub Actions to create and approve pull requests"**
7. Click **Save**

### 2. Enable GitHub Pages (Will auto-configure after first run)

The workflow will automatically create the `gh-pages` branch on first successful run.

After the first workflow completes:
1. In **Settings**, click **Pages** in left sidebar
2. Under **Source**, it should show **"Deploy from a branch"**
3. Branch should be: **gh-pages** / **/ (root)**
4. If not, select it and click **Save**

### 3. Re-run Failed Workflow

1. Go to **Actions** tab
2. Click on the failed workflow run
3. Click **Re-run all jobs** (top right)

---

## 🎯 Verification

After following the steps above:
1. Push a new commit or manually trigger the workflow
2. Go to **Actions** tab and watch the workflow run
3. Once complete (green ✅), go to **Settings** → **Pages**
4. You'll see: "Your site is live at `https://yourusername.github.io/restassured-api-testing/`"
5. Click the URL to view your Allure report

---

## 🔍 Common Issues & Solutions

### Issue: "Resource not accessible by integration"
**Cause:** Missing permissions
**Solution:** Follow Step 1 above to grant write permissions

### Issue: "refusing to allow a GitHub App to create or update workflow"
**Cause:** Protected branch rules
**Solution:** 
- Go to **Settings** → **Branches**
- If `gh-pages` branch has protection, temporarily disable it or add GitHub Actions as exception

### Issue: Pages not updating
**Cause:** Cache or deployment delay
**Solution:**
- Wait 1-2 minutes after workflow completes
- Hard refresh browser (Cmd+Shift+R on Mac, Ctrl+Shift+R on Windows)
- Check deployment status in **Settings** → **Pages**

### Issue: 404 when accessing GitHub Pages URL
**Cause:** GitHub Pages not properly configured
**Solution:**
1. Verify **Settings** → **Pages** shows "Your site is live"
2. Ensure `gh-pages` branch exists
3. Wait a few minutes for initial deployment
4. Check URL format: `https://<username>.github.io/<repo-name>/`

### Issue: Workflow runs but report is empty
**Cause:** Tests may have failed or no test results generated
**Solution:**
1. Check test execution logs in Actions
2. Run tests locally first: `mvn clean test`
3. Verify `target/allure-results/` has files
4. Check workflow artifacts for actual test results

---

## 📋 Quick Checklist

Before pushing to GitHub, ensure:
- ✅ Workflow file exists: `.github/workflows/api-tests.yml`
- ✅ Tests run locally: `mvn clean test`
- ✅ Allure results generated: `target/allure-results/` has files
- ✅ Repository permissions set to "Read and write"
- ✅ GitHub Pages enabled with `gh-pages` branch
- ✅ Branch names in workflow match your branches (main/develop)

---

## 🚀 Manual Workflow Trigger

If you want to run the workflow manually:
1. Go to **Actions** tab
2. Click **API Tests with Allure Report** workflow
3. Click **Run workflow** button (right side)
4. Select branch
5. Click **Run workflow**

---

## 📊 Viewing Reports

### From GitHub Pages
- URL: `https://<username>.github.io/<repo-name>/`
- Updates automatically on each workflow run
- Shows last 20 test runs with history

### From Artifacts
1. Go to **Actions** tab
2. Click on any completed workflow run
3. Scroll to **Artifacts** section
4. Download:
   - **allure-report** - Complete HTML report (zip)
   - **allure-results** - Raw JSON results
   - **surefire-reports** - TestNG XML reports

---

## 🔐 Security Notes

- `GITHUB_TOKEN` is automatically provided by GitHub Actions
- No need to create personal access tokens
- Permissions are scoped to the repository only
- Token expires after workflow completes

---

## 🆘 Still Having Issues?

1. **Check workflow logs:**
   - Go to Actions → Failed run → Click on job → Expand steps

2. **Verify file structure:**
   ```bash
   git ls-files .github/
   # Should show: .github/workflows/api-tests.yml
   ```

3. **Test locally first:**
   ```bash
   mvn clean test
   ls -la target/allure-results/
   allure serve target/allure-results
   ```

4. **Check GitHub Status:**
   - Visit [GitHub Status](https://www.githubstatus.com/)
   - Ensure Actions and Pages services are operational

---

## ✨ Updated Workflow Features

The workflow now includes:
- ✅ Proper permissions configuration
- ✅ User identity for commits
- ✅ Updated actions versions (v4)
- ✅ Multiple artifact uploads
- ✅ Error handling with `continue-on-error`
- ✅ History retention (last 20 reports)

---

## 📚 Additional Resources

- [GitHub Actions Permissions](https://docs.github.com/en/actions/security-guides/automatic-token-authentication#permissions-for-the-github_token)
- [GitHub Pages Documentation](https://docs.github.com/en/pages)
- [Allure Report Action](https://github.com/simple-elf/allure-report-action)
- [Peaceiris GitHub Pages Action](https://github.com/peaceiris/actions-gh-pages)

---

## 🎉 Success Indicators

Your setup is working correctly when:
- ✅ Workflow shows green checkmark in Actions
- ✅ `gh-pages` branch exists with report files
- ✅ GitHub Pages shows "Your site is live"
- ✅ Report URL loads with test results
- ✅ New commits trigger automatic report updates

Happy Testing! 🚀
