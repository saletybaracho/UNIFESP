/* 
 * Sourcerer: an infrastructure for large-scale source code analysis.
 * Copyright (C) by contributors. See CONTRIBUTORS.txt for full list.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package edu.uci.ics.sourcerer.tools.java.repo.model.extracted;

import java.util.Collection;

import edu.uci.ics.sourcerer.tools.core.repo.model.ModifiableRepository;
import edu.uci.ics.sourcerer.tools.core.repo.model.ProjectLocation;
import edu.uci.ics.sourcerer.tools.java.repo.model.JarFile;
import edu.uci.ics.sourcerer.tools.java.repo.model.JavaProject;

/**
 * @author Joel Ossher (jossher@uci.edu)
 */
public interface ModifiableExtractedJavaRepository extends ExtractedJavaRepository, ModifiableRepository {
  @Override
  public Collection<? extends ModifiableExtractedJavaBatch> getBatches();
  
  @Override
  public ModifiableExtractedJarFile getJarFile(String hash);
  
  @Override
  public ModifiableExtractedJavaProject getProject(String path);
  
  @Override
  public ModifiableExtractedJavaProject getProject(Integer batch, Integer checkout);
  
  @Override
  public ModifiableExtractedJavaProject getProject(ProjectLocation loc);

  @Override
  public Collection<? extends ModifiableExtractedJavaProject> getProjects();
  
  public ModifiableExtractedJavaProject getMatchingProject(JavaProject project);
  
  public ModifiableExtractedJarFile getMatchingJarFile(JarFile jar);
}
