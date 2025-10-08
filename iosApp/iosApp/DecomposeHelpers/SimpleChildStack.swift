//
//  SimpleChildStack.swift
//  iosApp
//
//  Created by Ruslan Yet on 08.10.25.
//

import Shared

func simpleChildStack<T : AnyObject>(_ child: T) -> Value<ChildStack<AnyObject, T>> {
    return mutableValue(
        ChildStack(
            configuration: "config" as AnyObject,
            instance: child
        )
    )
}
