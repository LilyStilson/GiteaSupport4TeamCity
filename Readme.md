# Gitea support for TeamCity
*Test task for internship application*

## Description
For the sake of simplicity, this is not a proper *library* implementation. 
It's a mix of package and application. `package` declaration in `Git.kt` 
will show a warning of incorrect path.

## Tests
The only untested file is `Main.kt`, everything else has 100% coverage.
![image](https://github.com/LilyStilson/GiteaSupport4TeamCity/assets/42270214/6fdc87e5-9037-46db-96a3-fdc15b493414)

## Usage
```kotlin
import org.nightskystudio.git.*

val git = Git()

// Add files to commit
git.addFiles(files = mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))

// Commit
git.commit(author = "Jan Novak", message = "Initial commit")

// Initialize tree and add files to it
val tree = Tree()
git.addFiles(tree, mapOf("file.txt" to Either.Left(Blob("Hello, world!"))))

// Add another tree to commit tree
val tree2 = Tree()
git.addFiles(tree2, mapOf("file1.txt" to Either.Left(Blob("Hello, world!"))))
git.addFiles(tree, mapOf("tree.txt" to Either.Right(tree2)))

// Commit with tree
git.commit(tree, "Jan Novak", "2nd commit")

// List commits
val commits = git.listCommits()
commits.forEach { println(it) }

/* Find commit by predicate
 * Possible options are:
 * hash:    { it.hash == commit.hash }
 * author:  { it.author == commit.author }
 * message: { it.message == commit.message }
 * time:    { it.time == commit.time }
 * tree:    { it.tree == commit.tree }
 */
val commit = git.findCommit { it.hash == commits[0].hash }
println(commit)

// Find all commits by predicate
// Possible values are the same as in findCommit
val commits2 = git.findCommits { it.author == "Jan Novak" }
commits2.forEach { println(it) }
```
