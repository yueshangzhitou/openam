/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */
package org.forgerock.openam.selfservice;

import static org.forgerock.openam.selfservice.SelfServiceGuiceModule.INTERIM_TYPE;

import org.forgerock.http.Context;
import org.forgerock.json.resource.AbstractRequestHandler;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.ActionResponse;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.RequestHandler;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResourceResponse;
import org.forgerock.openam.selfservice.SelfServiceGuiceModule.InterimConfig;
import org.forgerock.selfservice.core.AnonymousProcessService;
import org.forgerock.selfservice.core.ProcessStore;
import org.forgerock.selfservice.core.ProgressStageFactory;
import org.forgerock.selfservice.core.StorageType;
import org.forgerock.selfservice.core.config.ProcessInstanceConfig;
import org.forgerock.selfservice.core.snapshot.SnapshotTokenHandlerFactory;
import org.forgerock.util.promise.Promise;

import javax.inject.Inject;

/**
 * Utilises the common anonymous process service to deliver user registration behaviour.
 *
 * @since 13.0.0
 */
public final class UserRegistrationRequestHandler extends AbstractRequestHandler {

    private final RequestHandler anonymousProcess;

    @Inject
    public UserRegistrationRequestHandler(ProgressStageFactory stageFactory,
                                          SnapshotTokenHandlerFactory tokenHandlerFactory, ProcessStore localStore) {
        ProcessInstanceConfig config = ProcessInstanceConfig
                .newBuilder()
                .addStageConfig(new InterimConfig())
                .setStorageType(StorageType.STATELESS)
                .setTokenType(INTERIM_TYPE)
                .build();

        anonymousProcess = new AnonymousProcessService(config, stageFactory, tokenHandlerFactory, localStore);
    }

    @Override
    public Promise<ResourceResponse, ResourceException> handleRead(Context context, ReadRequest request) {
        return anonymousProcess.handleRead(context, request);
    }

    @Override
    public Promise<ActionResponse, ResourceException> handleAction(Context context, ActionRequest request) {
        return anonymousProcess.handleAction(context, request);
    }

}