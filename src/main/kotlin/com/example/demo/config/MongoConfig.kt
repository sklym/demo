package com.example.demo.config

import com.mongodb.MongoClientSettings
import com.mongodb.MongoCredential
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@Configuration
@EnableReactiveMongoRepositories(basePackages = ["com.example.demo.repository"])
 class MongoConfig : AbstractReactiveMongoConfiguration() {
    @Value("\${mongo.port}")
    private val port: String? = null

    @Value("\${mongo.dbname}")
    private val dbName: String? = null

    @Value("\${mongo.user}")
    private val user: String? = null

    @Value("\${mongo.pass}")
    private val pass: String? = null

    @Value("\${mongo.authdb}")
    private val authdb: String? = null

    override fun reactiveMongoClient(): MongoClient {
        if(user == null || pass == null || authdb == null)
            return MongoClients.create();
        else
        return MongoClients.create(
            MongoClientSettings.builder()
                .credential(MongoCredential.createCredential(user as String, authdb as String, pass!!.toCharArray())).build()
        )
    }

    override fun getDatabaseName(): String {
        return dbName!!
    }

    @Bean
     fun reactiveMongoTemplate(): ReactiveMongoTemplate {
        return ReactiveMongoTemplate(reactiveMongoClient(), databaseName)
    }
}