//
//  TBLAnimateView.m
//  Animit
//
//  Created by Mzalih on 31/03/14.
//  Copyright (c) 2014 toobler. All rights reserved.
//

#import "TBLAnimateView.h"
@implementation TBLAnimateView
int position1=-1;
int position2=-1;
float ANIMATIONSPEED =1;
float ANIMATIONDELAY =0.5;
- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}


-(void)setImages:(NSMutableArray *)passedImages1 :(NSMutableArray *)passedImages2 {
    //  ARGUMENTS IMAGE ARRAY TO LOAD
    _images1=passedImages1;
   // _images2=passedImages2;
}
-(void)animate1:(id)time{
    //  ANIMATE THE FIRST VIEW
    @try {
        int  i= [self getPosition1];
        _imageView1 = [self animate:_imageView1 withImage:[_images1 objectAtIndex:i]];
        [self performSelector:@selector(animate2:) withObject:self afterDelay:ANIMATIONDELAY];
    }
    @catch (NSException *exception) {
        
    }
    @finally {
        
    }
    
}
-(void)animate2:(id)time{
    //  ANIMATE THE FIRST VIEW
    @try {
        int  i= [self getPosition2];
        _imageView2 = [self animate:_imageView2 withImage:[_images2 objectAtIndex:i]];
        [self performSelector:@selector(animate1:) withObject:self afterDelay:ANIMATIONDELAY];
    }
    @catch (NSException *exception) {
        
    }
    @finally {
        
    }
}
-(UIImageView *)animate :(UIImageView *)view withImage:(UIImage *)image{
    // KEEP THE CURRENT VIEW IN THE VIEW
    UIImageView *currentView = view;
    //CREATE NEW VIEW TO ANIMATE IN
    view = [[UIImageView alloc]init];
    view.frame=CGRectMake(currentView.frame.origin.x, currentView.frame.origin.y, currentView.frame.size.width, currentView.frame.size.height);
    
    view.alpha=0;
    @try{
        view.image=image;
    }
    @catch (NSException *exception) {
        
    }
    [self addSubview:view];
    
    
    [UIView animateWithDuration:ANIMATIONSPEED delay:0.5 options:UIViewAnimationOptionCurveEaseInOut animations:^{
        [view.layer displayIfNeeded];
        
         currentView.alpha = !currentView.alpha;
        view.alpha=1;
        
       // view.frame=currentView.frame;

    }completion:^(BOOL done){
        [currentView removeFromSuperview];
    }];

       // RETURN THE NEW VIEW
    return view;
}
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
  
    // FIRST VIEW
    _imageView1 =[[UIImageView alloc]initWithFrame:(CGRectMake(0, 0, self.frame.size.width,self.frame.size.height))] ;
    @try {
        _imageView1.image =[_images1 objectAtIndex:[self getPosition1]] ;
    }
    @catch (NSException *exception) {
        
    }
    @finally {
        
    }
    
    [self addSubview: _imageView1];
 [self performSelector:@selector(animate1:) withObject:self afterDelay:ANIMATIONDELAY];
}
-(int)getPosition1{
    // RETURN FIRST POSITION
    if(position1 >=[_images1 count]-1){
        position1 =0;
    }else{
        position1++;
    }
    return position1;
}
-(int)getPosition2{
    //RETURN SECOND POSITION
    if(position2 >=[_images2 count]-1){
        position2=0;
    }else{
        position2++;
    }
    return position2;
}
@end
