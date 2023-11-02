package org.jorm.Jdbc.orm;

import com.google.auto.service.*;

import java.lang.reflect.Field;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.macnss.Libs.orm.AutoGenerateGetSet")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class AutoGenerateGetSetProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        System.out.println("AutoGenerateGetSetProcessor");
        for (Element element : roundEnv.getElementsAnnotatedWith(AutoGenerateGetSet.class)) {
            if (element.getKind() == ElementKind.FIELD) {
                generateGettersAndSetters((VariableElement) element);
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "@AutoGenerateGetSet can only be applied to fields", element);
            }
        }
        return true;
    }

    private void generateGettersAndSetters(VariableElement fieldElement) {
        String fieldName = fieldElement.getSimpleName().toString();
        String capitalizedFieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        String fieldType = fieldElement.asType().toString();

        // Generate getter method
        String getter = "public " + fieldType + " get" + capitalizedFieldName + "() {\n";
        getter += "    return this." + fieldName + ";\n";
        getter += "}\n";

        // Generate setter method
        String setter = "public void set" + capitalizedFieldName + "(" + fieldType + " " + fieldName + ") {\n";
        setter += "    this." + fieldName + " = " + fieldName + ";\n";
        setter += "}\n";

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                "Generated getter and setter for field: " + fieldName);

        // Add the generated methods to the class containing the annotated field
        TypeElement classElement = (TypeElement) fieldElement.getEnclosingElement();
        StringBuilder classSource = new StringBuilder();
        classSource.append(getter);
        classSource.append(setter);

        // Modify the class's source code to add the generated methods
        // Note: You may need a more sophisticated code generation approach for more complex classes
        // This is a basic example.
        // You could use a code generation library like JavaPoet for more advanced use cases.

        // Modify the class's source code to add the generated methods
        // Note: This is a very basic example. For a more robust solution, consider using a code generation library.
        String originalClassSource = classElement.toString();
        String modifiedClassSource = originalClassSource.replace("}", getter + setter + "\n}");

        // Print the modified source code (you might want to write it to a file)
        System.out.println(modifiedClassSource);
    }
}
