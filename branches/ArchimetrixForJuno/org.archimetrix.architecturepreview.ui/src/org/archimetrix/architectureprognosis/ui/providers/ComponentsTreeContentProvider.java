package org.archimetrix.architectureprognosis.ui.providers;


import java.util.ArrayList;
import java.util.List;

import org.archimetrix.architectureprognosis.ui.util.ComponentsUtil;
//import org.eclipse.gmt.modisco.java.ClassDeclaration;
import org.eclipse.gmt.modisco.java.Type;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

//import de.fzi.gast.types.GASTClass;
//import eu.qimpress.samm.staticstructure.ComponentType;
//import eu.qimpress.samm.staticstructure.CompositeComponent;
//import eu.qimpress.samm.staticstructure.PrimitiveComponent;
//import eu.qimpress.samm.staticstructure.Repository;
//import eu.qimpress.samm.staticstructure.SubcomponentInstance;
import org.somox.sourcecodedecorator.ComponentImplementingClassesLink;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.CompositeComponent;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.Repository;


/**
 * This class provides the content of the two components trees in the Architecture Prognosis View.
 * 
 * @author mcp
 * @author Last editor: $Author$
 * @version $Revision$ $Date$
 * 
 */
public class ComponentsTreeContentProvider implements ITreeContentProvider
{

   private static final String SOMOX_DUMMY_COMPONENT = "SoMoX System-Level Dummy Component";


   @Override
   public Object[] getElements(final Object inputElement)
   {
      List<RepositoryComponent> allComponents = ((Repository) inputElement).getComponents__Repository();
      List<RepositoryComponent> filteredComponents = new ArrayList<RepositoryComponent>();
      for (RepositoryComponent componentType : allComponents)
      {
         if (!componentType.getEntityName().equals(SOMOX_DUMMY_COMPONENT))
         {
            filteredComponents.add(componentType);
         }
      }
      return filteredComponents.toArray();
   }


   @Override
   public void dispose()
   {

   }


   @Override
   public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
   {

   }


   @Override
   public Object[] getChildren(final Object parentElement)
   {
      if (parentElement instanceof CompositeComponent)
      {
         CompositeComponent parent = (CompositeComponent) parentElement;
         List<RepositoryComponent> children = getChildrenComponents(parent);
         return children.toArray();
      }
      else if (parentElement instanceof BasicComponent) //  PrimitiveComponent changed to BasicComponent
      {
         BasicComponent parent = (BasicComponent) parentElement;
         ComponentImplementingClassesLink link = ComponentsUtil.get().getComponentImplementingClassesLinkForComponent(
               parent);
         if (link != null)
         {
            List<Type> children = link.getImplementingClasses();
            return children.toArray();
         }
      }
      return null;
   }


   private List<RepositoryComponent> getChildrenComponents(final CompositeComponent parent)
   {
      List<RepositoryComponent> children = new ArrayList<RepositoryComponent>();
      for (AssemblyContext subComp : parent.getAssemblyContexts__ComposedStructure())
      {
         children.add(subComp.getEncapsulatedComponent__AssemblyContext());
      }
      return children;
   }


   @Override
   public Object getParent(final Object element)
   {
      if (element instanceof BasicComponent || element instanceof CompositeComponent)
      {
         return getParentCompositeComponent((RepositoryComponent) element,
               ((Repository) ((RepositoryComponent) element).eContainer()).getComponents__Repository());
      }
      return null;
   }


   private RepositoryComponent getParentCompositeComponent(final RepositoryComponent element, final List<RepositoryComponent> components)
   {
      for (RepositoryComponent component : components)
      {
         if (component instanceof CompositeComponent)
         {
            List<RepositoryComponent> childrenComponents = getChildrenComponents((CompositeComponent) component);
            if (childrenComponents.contains(element))
            {
               return component;
            }
            else
            {
            	RepositoryComponent result = getParentCompositeComponent(element, childrenComponents);
               if (result != null)
               {
                  return result;
               }
            }
         }
      }
      return null;
   }


   @Override
   public boolean hasChildren(final Object element)
   {
      if (element instanceof CompositeComponent || element instanceof BasicComponent)
      {
         return true;
      }
      return false;
   }

}
