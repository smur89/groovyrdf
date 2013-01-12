package de.datenwissen.util.groovyrdf.jena

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.Property
import com.hp.hpl.jena.rdf.model.Resource
import com.hp.hpl.jena.rdf.model.ResourceFactory
import com.hp.hpl.jena.vocabulary.RDF
import com.hp.hpl.jena.vocabulary.RDFS
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Used to build a RDF resource. The resource might be nested in a property of another resource, like
 *
 * <pre>{@code<resource> vocab:Friend <other>}</pre>
 */
protected class JenaResourceBuilder extends JenaAbstractResourceBuilder {

    Logger log = LoggerFactory.getLogger (JenaResourceBuilder.class);

    private Resource resource

    protected void setParent (Object parent) {
        JenaNestedPropertyBuilder nested = (JenaNestedPropertyBuilder) parent
        Property currentProperty = nested.property
        Resource currentResource = nested.currentResource
        log.debug "adding resource $resource to property $currentProperty of resource $currentResource"
        currentResource.addProperty (currentProperty, resource)
    }

    @Override
    protected Object createNode (Model model, Object name) {
        return new JenaNestedPropertyBuilder (property: ResourceFactory.createProperty (name))
    }

    protected Object addPublicKey (Model model, String keyUri, String label, String modulus, int exponent) {
        def key = model.createResource (keyUri)
        key.addProperty (RDF.type, 'http://www.w3.org/ns/auth/cert#RSAPublicKey')
        if (label) {
            key.addProperty (RDFS.label, label)
        }
        key.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#modulus"), modulus, XSDDatatype.XSDhexBinary)
        key.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#exponent"), exponent.toString (), XSDDatatype.XSDinteger)
        resource.addProperty (ResourceFactory.createProperty ("http://www.w3.org/ns/auth/cert#key"), key)
    }

    @Override
    public String toString () {
        return "$resource"
    }

}
