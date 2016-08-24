//
//  TBLAnimateView.h
//  Animit
//
//  Created by Mzalih on 31/03/14.
//  Copyright (c) 2014 toobler. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface TBLAnimateView : UIView

//IMAGE VIEWS
@property(nonatomic)UIImageView *imageView1;
@property(nonatomic)UIImageView *imageView2;

//IMAGES ARRAY
@property(nonatomic,copy)NSMutableArray *images1;
@property(nonatomic,copy)NSMutableArray *images2;

//METHOD TO PASS IMAGES
-(void)setImages:(NSMutableArray *)passedImages1 :(NSMutableArray *)passedImages2 ;
@end
