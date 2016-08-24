//
//  NSAvailableMoods.h
//  Audioplayer
//
//  Created by Mzalih on 21/10/14.
//  Copyright (c) 2014 Toobler. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSAvailableMoods : NSObject

+(NSAvailableMoods *)getInstance;
-(NSMutableArray *)getAvailableMoods;
@end
