package org.pofo.api.config

import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import org.pofo.common.error.CustomError
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter
import org.springframework.stereotype.Component

@Component
class GlobalGraphQLExceptionHandler : DataFetcherExceptionResolverAdapter() {
    override fun resolveToSingleError(
        ex: Throwable,
        env: DataFetchingEnvironment,
    ): GraphQLError? {
        if (ex is CustomError) {
            return GraphQLError
                .newError()
                .message(ex.message)
                .path(env.executionStepInfo.path)
                .location(env.field.sourceLocation)
                .build()
        }

        // Default to the super implementation for other exceptions
        return super.resolveToSingleError(ex, env)
    }
}
